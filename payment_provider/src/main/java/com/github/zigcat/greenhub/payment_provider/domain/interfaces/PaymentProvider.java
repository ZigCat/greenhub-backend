package com.github.zigcat.greenhub.payment_provider.domain.interfaces;

import reactor.core.publisher.Mono;

public interface PaymentProvider {
    String getName();
    Mono<String> createSubscription(String email, String planId);
    Mono<Void> cancelSubscription(String subscriptionId);
}
