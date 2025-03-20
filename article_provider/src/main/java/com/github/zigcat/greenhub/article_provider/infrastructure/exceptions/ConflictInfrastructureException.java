package com.github.zigcat.greenhub.article_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.article_provider.exceptions.ClientErrorException;

public class ConflictInfrastructureException extends ClientErrorException {
    public ConflictInfrastructureException(String message) {
        super(message, 409);
    }
}
