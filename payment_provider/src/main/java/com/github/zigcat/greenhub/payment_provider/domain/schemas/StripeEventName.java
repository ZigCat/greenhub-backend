package com.github.zigcat.greenhub.payment_provider.domain.schemas;

import lombok.Getter;

@Getter
public enum StripeEventName {
    SESSION_COMPLETED("checkout.session.completed"),
    PAYMENT_SUCCEEDED("invoice.payment_succeeded"),
    PAYMENT_FAILED("invoice.payment_failed"),
    SUBSCRIPTION_UPDATED("customer.subscription.updated"),
    SUBSCRIPTION_DELETED("customer.subscription.deleted");

    private final String event;

    StripeEventName(String event) {
        this.event = event;
    }
}
