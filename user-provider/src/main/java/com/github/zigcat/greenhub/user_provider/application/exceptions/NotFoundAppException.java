package com.github.zigcat.greenhub.user_provider.application.exceptions;

import com.github.zigcat.greenhub.user_provider.config.CoreException;

public class NotFoundAppException extends CoreException {
    public NotFoundAppException(String message) {
        super(message, 404);
    }
}
