package com.github.zigcat.greenhub.api_gateway.application.exceptions;

import com.github.zigcat.greenhub.api_gateway.exceptions.CoreException;

public class ServerErrorAppException extends CoreException {
    public ServerErrorAppException(String message) {
        super(message, 500);
    }
}
