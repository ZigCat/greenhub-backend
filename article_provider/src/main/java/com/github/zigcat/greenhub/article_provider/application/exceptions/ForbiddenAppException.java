package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.config.CoreException;

public class ForbiddenAppException extends CoreException {
    public ForbiddenAppException(String message) {
        super(message, 403);
    }
}
