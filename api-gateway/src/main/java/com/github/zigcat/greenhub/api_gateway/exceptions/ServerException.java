package com.github.zigcat.greenhub.api_gateway.exceptions;

import com.github.zigcat.greenhub.api_gateway.exceptions.base.GreenhubException;

public class ServerException extends GreenhubException {
    public ServerException(String message) {
        super(message, 500);
    }
}
