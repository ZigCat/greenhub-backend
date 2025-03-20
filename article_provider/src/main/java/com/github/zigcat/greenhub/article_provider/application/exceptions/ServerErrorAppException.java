package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.exceptions.ServerErrorException;

public class ServerErrorAppException extends ServerErrorException {
    public ServerErrorAppException(String message) {
        super(message, 500);
    }
}
