package com.github.zigcat.greenhub.user_provider.application.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.ServerErrorException;

public class ServerErrorAppException extends ServerErrorException {
    public ServerErrorAppException(String message) {
        super(message, 500);
    }
}
