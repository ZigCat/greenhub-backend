package com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.auth_provider.exceptions.ClientErrorException;

public class NotFoundInfrastructureException extends ClientErrorException {
    public NotFoundInfrastructureException(String message) {
        super(message, 404);
    }
}
