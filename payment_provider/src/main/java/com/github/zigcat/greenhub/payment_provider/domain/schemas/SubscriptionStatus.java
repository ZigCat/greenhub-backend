package com.github.zigcat.greenhub.payment_provider.domain.schemas;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    ACTIVE("ACTIVE"),
    CANCEL_AWAITING("CANCEL_AWAITING"),
    CANCELED("CANCELED"),
    EXPIRED("EXPIRED"),
    PAYMENT_FAILED("PAYMENT_FAILED"),
    PENDING("PENDING");
    private final String value;

    SubscriptionStatus(String value) {
        this.value = value;
    }
}
