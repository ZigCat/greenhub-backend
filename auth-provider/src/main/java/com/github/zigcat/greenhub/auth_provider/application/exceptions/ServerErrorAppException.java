package com.github.zigcat.greenhub.auth_provider.application.exceptions;

import com.github.zigcat.greenhub.auth_provider.exceptions.ServerErrorException;

public class ServerErrorAppException extends ServerErrorException {
    public ServerErrorAppException(String message) {
        super(message, 500);
    }
}
