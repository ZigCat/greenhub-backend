package com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.auth_provider.exceptions.ServerErrorException;

public class ServiceUnavailableInfrastructureException extends ServerErrorException {
    public ServiceUnavailableInfrastructureException(String message) {
        super(message, 503);
    }
}
