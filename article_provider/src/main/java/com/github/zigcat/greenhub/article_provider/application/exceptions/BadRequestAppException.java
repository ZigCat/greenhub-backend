package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.config.CoreException;

public class BadRequestAppException extends CoreException {
    public BadRequestAppException(String message) {
        super(message, 400);
    }
}
