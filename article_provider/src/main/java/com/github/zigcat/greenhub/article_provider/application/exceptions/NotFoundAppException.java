package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.config.CoreException;

public class NotFoundAppException extends CoreException {
    public NotFoundAppException(String message) {
        super(message, 404);
    }
}
