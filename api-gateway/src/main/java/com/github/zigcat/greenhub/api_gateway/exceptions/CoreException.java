package com.github.zigcat.greenhub.api_gateway.exceptions;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException{
    private int code;
    public CoreException(String message, int code) {
        super(message);
        this.code = code;
    }
}
