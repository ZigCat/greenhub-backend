package com.github.zigcat.greenhub.payment_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.payment_provider.domain.interfaces.PaymentProvider;
import com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions.SourceInfrastructureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class PaypalPaymentProvider implements PaymentProvider {
    @Value("${paypal.key.client}")
    private String PAYPAL_CLIENT;
    @Value("${paypal.key.secret}")
    private String PAYPAL_SECRET;
    @Value("${paypal.url}")
    private String BASE_URL;
    private final WebClient webClient;
    private final AtomicReference<Mono<String>> cachedToken = new AtomicReference<>();

    public PaypalPaymentProvider(WebClient.Builder externalBuilder) {
        this.webClient = externalBuilder.baseUrl(BASE_URL).build();
        this.cachedToken.set(fetchNewToken());
    }

    private Mono<String> fetchNewToken() {
        return webClient.post()
                .uri("/v1/oauth2/token")
                .headers(headers -> headers.setBasicAuth(PAYPAL_CLIENT, PAYPAL_SECRET))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .cache(Duration.ofHours(8))
                .onErrorMap(e -> {
                    cachedToken.set(null);
                    throw new SourceInfrastructureException("Paypal services are unavailable");
                });
    }

    private Mono<String> getAccessToken() {
        Mono<String> existingToken = cachedToken.get();
        if (existingToken != null) return existingToken;
        synchronized (this) {
            if (cachedToken.get() != null) return cachedToken.get();
            Mono<String> newToken = fetchNewToken();
            cachedToken.set(newToken);
            return newToken;
        }
    }

    @Override
    public String getName() {
        return "paypal";
    }

    @Override
    public Mono<String> createSubscription(String email, String planId) {
        return getAccessToken().flatMap(token ->
                webClient.post()
                        .uri("/v1/billing/subscriptions")
                        .headers(headers -> headers.setBearerAuth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "plan_id", planId,
                                "subscriber", Map.of("email_address", email),
                                "application_context", Map.of(
                                        "brand_name", "GreenInsight",
                                        "return_url", "https://github.com",
                                        "cancel_url", "https://google.com",
                                        "user_action", "SUBSCRIBE_NOW")
                        ))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(response -> (String) response.get("id"))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> cancelSubscription(String subscriptionId) {
        return getAccessToken().flatMap(token ->
                webClient.post()
                        .uri("/v1/billing/subscriptions/{id}/cancel", subscriptionId)
                        .headers(headers -> headers.setBearerAuth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("reason", "User request"))
                        .retrieve()
                        .bodyToMono(Void.class)
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
