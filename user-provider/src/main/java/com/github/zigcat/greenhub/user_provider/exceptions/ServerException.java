package com.github.zigcat.greenhub.user_provider.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.base.GreenhubException;

public class ServerException extends GreenhubException {
    public ServerException(String message) {
        super(message, 500);
    }
}
