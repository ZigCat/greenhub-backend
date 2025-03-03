package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.config.CoreException;

public class ConflictAppException extends CoreException {
    public ConflictAppException(String message) {
        super(message, 409);
    }
}
