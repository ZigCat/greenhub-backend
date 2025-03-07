package com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.auth_provider.config.CoreException;

public class ServerErrorInfrastructureException extends CoreException {
    public ServerErrorInfrastructureException(String message) {
        super(message, 503);
    }
}
