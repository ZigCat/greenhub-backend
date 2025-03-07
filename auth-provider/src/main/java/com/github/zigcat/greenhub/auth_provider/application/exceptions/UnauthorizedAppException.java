package com.github.zigcat.greenhub.auth_provider.application.exceptions;

import com.github.zigcat.greenhub.auth_provider.exceptions.ClientErrorException;

public class UnauthorizedAppException extends ClientErrorException {
    public UnauthorizedAppException(String message) {
        super(message, 401);
    }
}
