package com.github.zigcat.greenhub.api_gateway.infrastructure.exceptions;

import com.github.zigcat.greenhub.api_gateway.exceptions.CoreException;

public class ServerErrorInfrastructureException extends CoreException {
    public ServerErrorInfrastructureException(String message) {
        super(message, 500);
    }
}
