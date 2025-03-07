package com.github.zigcat.greenhub.user_provider.application.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.ClientErrorException;

public class AuthAppException extends ClientErrorException {
    public AuthAppException(String message) {
        super(message, 401);
    }
}
