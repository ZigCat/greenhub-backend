package com.github.zigcat.greenhub.article_provider.domain.schemas;

import lombok.Getter;

@Getter
public enum PaidStatus {
    PAID("PAID"),
    FREE("FREE");

    private final String value;

    PaidStatus(String value) {
        this.value = value;
    }
}
