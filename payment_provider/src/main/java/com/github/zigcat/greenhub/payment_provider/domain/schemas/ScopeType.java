package com.github.zigcat.greenhub.payment_provider.domain.schemas;

import lombok.Getter;

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

    ScopeType(String scope) {
        this.scope = scope;
    }
}