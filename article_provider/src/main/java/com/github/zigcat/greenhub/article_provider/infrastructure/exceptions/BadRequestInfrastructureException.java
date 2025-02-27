package com.github.zigcat.greenhub.article_provider.infrastructure.exceptions;

import com.github.zigcat.greenhub.article_provider.config.CoreException;

public class BadRequestInfrastructureException extends CoreException {
    public BadRequestInfrastructureException(String message) {
        super(message, 400);
    }
}
