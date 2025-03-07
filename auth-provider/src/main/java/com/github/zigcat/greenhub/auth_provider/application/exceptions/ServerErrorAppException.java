package com.github.zigcat.greenhub.auth_provider.application.exceptions;

import com.github.zigcat.greenhub.auth_provider.config.CoreException;

public class ServerErrorAppException extends CoreException {
    public ServerErrorAppException(String message) {
        super(message, 500);
    }
}
