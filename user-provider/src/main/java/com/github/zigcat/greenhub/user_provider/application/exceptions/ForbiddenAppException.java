package com.github.zigcat.greenhub.user_provider.application.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.ClientErrorException;

public class ForbiddenAppException extends ClientErrorException {
    public ForbiddenAppException(String message) {
        super(message, 403);
    }
}
