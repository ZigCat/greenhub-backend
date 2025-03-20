package com.github.zigcat.greenhub.user_provider.domain.schemas;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ScopeType {
    USER_READ("user.read"),
    USER_MANAGE("user.manage"),
    ARTICLE_READ("article.read"),
    ARTICLE_WRITE("article.write"),
    ARTICLE_MANAGE("article.manage"),
    PAYMENT_VIEW("payment.view"),
    PAYMENT_CAPTURE("payment.capture"),
    PAYMENT_MANAGE("payment.manage");


    private String scope;

    public static List<ScopeType> selfGranted = List.of(
            ScopeType.ARTICLE_WRITE
    );

    ScopeType(String scope) {
        this.scope = scope;
    }

    public static ScopeType fromString(String value) {
        return Arrays.stream(values())
                .filter(s -> s.scope.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid scope: " + value));
    }
}
