package com.github.zigcat.greenhub.auth_provider.exceptions;

public class JwtAuthException extends RuntimeException{
    public JwtAuthException(String message) {
        super(message);
    }
}
