package com.github.zigcat.greenhub.auth_provider.exceptions;

import com.github.zigcat.greenhub.auth_provider.exceptions.base.GreenhubException;

public class JwtAuthException extends GreenhubException {
    public JwtAuthException(String message) {
        super(message, 400);
    }
}
