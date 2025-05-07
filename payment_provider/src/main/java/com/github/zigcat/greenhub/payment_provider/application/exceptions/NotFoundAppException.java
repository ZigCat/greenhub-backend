package com.github.zigcat.greenhub.payment_provider.application.exceptions;

import com.github.zigcat.greenhub.payment_provider.exceptions.ClientErrorException;

public class NotFoundAppException extends ClientErrorException {
    public NotFoundAppException(String message) {
        super(message, 404);
    }
}
