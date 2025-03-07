package com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.auth_provider.config.CoreException;

public class JwtAuthInfrastructureException extends CoreException {
    public JwtAuthInfrastructureException(String message) {
        super(message, 400);
    }
}
