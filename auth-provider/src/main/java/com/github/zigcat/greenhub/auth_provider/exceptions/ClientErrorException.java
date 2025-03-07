package com.github.zigcat.greenhub.auth_provider.exceptions;

public class ClientErrorException extends CoreException {
    public ClientErrorException(String message, int code) {
        super(message, code);
    }
}
