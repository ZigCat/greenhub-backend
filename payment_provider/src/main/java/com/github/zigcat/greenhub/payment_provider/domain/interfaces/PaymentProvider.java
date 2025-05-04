package com.github.zigcat.greenhub.payment_provider.domain.interfaces;

import com.github.zigcat.greenhub.payment_provider.domain.PaymentSession;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ProviderName;
import reactor.core.publisher.Mono;

public interface PaymentProvider {
    ProviderName getName();
    Mono<PaymentSession> createSubscription(String email, String planId);
    Mono<Void> cancelSubscription(String subscriptionId);
}
