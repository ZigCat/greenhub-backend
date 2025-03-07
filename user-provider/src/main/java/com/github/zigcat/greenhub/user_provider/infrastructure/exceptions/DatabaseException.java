package com.github.zigcat.greenhub.user_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.ServerErrorException;

public class DatabaseException extends ServerErrorException {
    public DatabaseException(String message) {
        super(message, 503);
    }
}
