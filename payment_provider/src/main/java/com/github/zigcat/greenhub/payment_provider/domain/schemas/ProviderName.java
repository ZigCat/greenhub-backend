package com.github.zigcat.greenhub.payment_provider.domain.schemas;

import lombok.Getter;

@Getter
public enum ProviderName {
    STRIPE("STRIPE"),
    PAYPAL("PAYPAL");

    private String value;

    ProviderName(String value) {
        this.value = value;
    }
}
