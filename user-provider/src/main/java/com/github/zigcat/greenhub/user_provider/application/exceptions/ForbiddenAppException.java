package com.github.zigcat.greenhub.user_provider.application.exceptions;

import com.github.zigcat.greenhub.user_provider.config.CoreException;

public class ForbiddenAppException extends CoreException {
    public ForbiddenAppException(String message) {
        super(message, 403);
    }
}
