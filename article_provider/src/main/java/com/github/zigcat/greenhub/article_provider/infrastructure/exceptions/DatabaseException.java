package com.github.zigcat.greenhub.article_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.article_provider.exceptions.ServerErrorException;

public class DatabaseException extends ServerErrorException {
    public DatabaseException(String message) {
        super(message, 503);
    }
}
