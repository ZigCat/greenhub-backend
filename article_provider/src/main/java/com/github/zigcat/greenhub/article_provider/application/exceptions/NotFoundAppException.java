package com.github.zigcat.greenhub.article_provider.application.exceptions;

import com.github.zigcat.greenhub.article_provider.exceptions.ClientErrorException;

public class NotFoundAppException extends ClientErrorException {
    public NotFoundAppException(String message) {
        super(message, 404);
    }
}
