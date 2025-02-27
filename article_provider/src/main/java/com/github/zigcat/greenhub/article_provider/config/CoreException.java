package com.github.zigcat.greenhub.article_provider.config;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {
    private final int statusCode;

    public CoreException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
