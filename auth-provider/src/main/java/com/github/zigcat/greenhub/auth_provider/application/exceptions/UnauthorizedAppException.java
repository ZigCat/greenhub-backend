package com.github.zigcat.greenhub.auth_provider.application.exceptions;

import com.github.zigcat.greenhub.auth_provider.config.CoreException;

public class UnauthorizedAppException extends CoreException {
    public UnauthorizedAppException(String message) {
        super(message, 401);
    }
}
