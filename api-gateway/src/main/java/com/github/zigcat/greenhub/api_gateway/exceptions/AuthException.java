package com.github.zigcat.greenhub.api_gateway.exceptions;

public class AuthException extends RuntimeException {
    public AuthException() {
        super("401: Unauthorized access");
    }

    public AuthException(String message) {
        super("401: " + message);
    }
}
