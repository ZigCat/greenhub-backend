package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.exceptions.ClientErrorException;

public class BadRequestAppException extends ClientErrorException {
    public BadRequestAppException(String message) {
        super(message, 400);
    }
}
