package com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.auth_provider.exceptions.ServerErrorException;

public class ServerErrorInfrastructureException extends ServerErrorException {
    public ServerErrorInfrastructureException(String message) {
        super(message, 503);
    }
}
