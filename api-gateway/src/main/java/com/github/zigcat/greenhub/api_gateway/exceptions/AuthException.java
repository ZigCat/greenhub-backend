package com.github.zigcat.greenhub.api_gateway.exceptions;

import com.github.zigcat.greenhub.api_gateway.exceptions.base.GreenhubException;

public class AuthException extends GreenhubException {
    public AuthException(String message) {
        super(message, 401);
    }
}
