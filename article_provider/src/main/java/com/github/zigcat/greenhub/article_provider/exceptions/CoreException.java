package com.github.zigcat.greenhub.article_provider.exceptions;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {
    private final int statusCode;

    public CoreException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
