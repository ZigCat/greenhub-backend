package com.github.zigcat.greenhub.api_gateway.exceptions.base;

import lombok.Getter;

@Getter
public class GreenhubException extends RuntimeException {
    private final int code;

    public GreenhubException(String message, int code) {
        super(message);
        this.code = code;
    }
}
