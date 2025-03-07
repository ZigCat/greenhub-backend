package com.github.zigcat.greenhub.user_provider.application.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.ClientErrorException;

public class BadRequestAppException extends ClientErrorException {
    public BadRequestAppException(String message) {
        super(message, 400);
    }
}
