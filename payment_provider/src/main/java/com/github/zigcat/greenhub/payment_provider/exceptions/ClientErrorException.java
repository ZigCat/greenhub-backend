package com.github.zigcat.greenhub.payment_provider.exceptions;

public class ClientErrorException extends CoreException{
    public ClientErrorException(String message, int code) {
        super(message, code);
    }
}
