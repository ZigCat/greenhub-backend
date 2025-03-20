package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.exceptions.ClientErrorException;

public class ConflictAppException extends ClientErrorException {
    public ConflictAppException(String message) {
        super(message, 409);
    }
}
