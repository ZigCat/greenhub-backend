package com.github.zigcat.greenhub.payment_provider.domain.schemas;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    ACTIVE("ACTIVE"),
    CANCELED("CANCELED"),
    EXPIRED("EXPIRED");
    private String value;

    SubscriptionStatus(String value) {
        this.value = value;
    }
}
