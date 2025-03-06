package com.github.zigcat.greenhub.user_provider.config;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException{
    private final int code;

    public CoreException(String message, int code) {
        super(message);
        this.code = code;
    }
}
