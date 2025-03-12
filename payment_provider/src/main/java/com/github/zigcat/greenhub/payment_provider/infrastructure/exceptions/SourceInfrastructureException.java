package com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.payment_provider.exceptions.ServerErrorException;

public class SourceInfrastructureException extends ServerErrorException {
    public SourceInfrastructureException(String message) {
        super(message, 503);
    }
}
