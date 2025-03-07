package com.github.zigcat.greenhub.api_gateway.infrastructure.exceptions;

import com.github.zigcat.greenhub.api_gateway.exceptions.CoreException;

public class ServiceUnavailableInfrastructureException extends CoreException {
    public ServiceUnavailableInfrastructureException(String message) {
        super(message, 503);
    }
}
