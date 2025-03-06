package com.github.zigcat.greenhub.user_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.user_provider.config.CoreException;

public class BadRequestInfrastructureException extends CoreException {
    public BadRequestInfrastructureException(String message) {
        super(message, 400);
    }
}
