package com.github.zigcat.greenhub.auth_provider.application.exceptions;

import com.github.zigcat.greenhub.auth_provider.exceptions.ClientErrorException;

public class BadRequestAppException extends ClientErrorException {
    public BadRequestAppException(String message) {
        super(message, 400);
    }
}
