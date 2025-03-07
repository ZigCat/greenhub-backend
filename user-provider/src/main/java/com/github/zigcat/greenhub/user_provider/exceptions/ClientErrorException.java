package com.github.zigcat.greenhub.user_provider.exceptions;

public class ClientErrorException extends CoreException {
    public ClientErrorException(String message, int code) {
        super(message, code);
    }
}
