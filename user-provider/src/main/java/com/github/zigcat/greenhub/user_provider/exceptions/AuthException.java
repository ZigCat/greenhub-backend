package com.github.zigcat.greenhub.user_provider.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.base.GreenhubException;

public class AuthException extends GreenhubException {
    public AuthException(String message) {
        super(message, 401);
    }
}
