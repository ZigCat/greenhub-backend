package com.github.zigcat.greenhub.article_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.article_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.SubscriptionRepository;
import com.github.zigcat.greenhub.article_provider.exceptions.ClientErrorException;
import com.github.zigcat.greenhub.article_provider.exceptions.ServerErrorException;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SubscriptionWebClientAdapter implements SubscriptionRepository {
    private final WebClient webClient;

    public SubscriptionWebClientAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("lb://payment-provider").build();
    }

    @Override
    public Mono<AppSubscription> retrieve(AuthorizationData auth) {
        return webClient.get()
                .uri("/protected/subscription")
                .header("X-User-Id", auth.getId().toString())
                .header("X-Username", auth.getUsername())
                .header("X-User-Role", auth.getRole().toString())
                .header("X-User-Scopes", auth.getScopes())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                    response.bodyToMono(DTO.ApiError.class)
                        .map(data -> new ClientErrorException(data.message(), data.status())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                    response.bodyToMono(DTO.ApiError.class)
                        .map(data -> new ServerErrorException(data.message(), data.status())))
                .bodyToMono(AppSubscription.class);
    }
}
