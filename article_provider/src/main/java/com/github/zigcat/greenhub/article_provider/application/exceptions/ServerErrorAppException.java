package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.config.CoreException;

public class ServerErrorAppException extends CoreException {
    public ServerErrorAppException(String message) {
        super(message, 500);
    }
}
