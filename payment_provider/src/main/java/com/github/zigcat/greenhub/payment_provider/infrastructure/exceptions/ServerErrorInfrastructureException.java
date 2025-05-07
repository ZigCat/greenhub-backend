package com.github.zigcat.greenhub.payment_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.payment_provider.exceptions.ServerErrorException;

public class ServerErrorInfrastructureException extends ServerErrorException {
    public ServerErrorInfrastructureException(String message) {
        super(message, 500);
    }
}
