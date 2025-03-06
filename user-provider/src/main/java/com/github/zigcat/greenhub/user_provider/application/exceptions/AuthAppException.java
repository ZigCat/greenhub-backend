package com.github.zigcat.greenhub.user_provider.application.exceptions;

import com.github.zigcat.greenhub.user_provider.config.CoreException;

public class AuthAppException extends CoreException {
    public AuthAppException(String message) {
        super(message, 401);
    }
}
