package com.github.zigcat.greenhub.user_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.user_provider.config.CoreException;

public class DatabaseException extends CoreException {
    public DatabaseException(String message) {
        super(message, 503);
    }
}
