package com.github.zigcat.greenhub.user_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.ClientErrorException;

public class BadRequestInfrastructureException extends ClientErrorException {
    public BadRequestInfrastructureException(String message) {
        super(message, 400);
    }
}
