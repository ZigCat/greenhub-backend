package com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.auth_provider.exceptions.ClientErrorException;

public class JwtAuthInfrastructureException extends ClientErrorException {
    public JwtAuthInfrastructureException(String message) {
        super(message, 403);
    }
}
