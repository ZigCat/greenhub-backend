package com.github.zigcat.greenhub.payment_provider.domain.interfaces;

import com.github.zigcat.greenhub.payment_provider.domain.AppSubscription;
import com.github.zigcat.greenhub.payment_provider.domain.PaymentSession;
import com.github.zigcat.greenhub.payment_provider.domain.StripeEvent;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public interface PaymentProvider {
    ProviderName getName();
    Mono<PaymentSession> createSubscription(String email, Long id, String planId);
    Mono<Void> cancelSubscription(String subscriptionId);
    Mono<Void> resumeSubscription(String subscriptionId);
    Mono<Void> refundSubscription(String subscriptionId);
    Mono<StripeEvent> handleWebhook(ServerHttpRequest request, String payload);
}
