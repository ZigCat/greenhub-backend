package com.github.zigcat.greenhub.user_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.user_provider.config.CoreException;

public class NotFoundInfrastructureException extends CoreException {
    public NotFoundInfrastructureException(String message) {
        super(message, 404);
    }
}
