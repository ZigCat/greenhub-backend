package com.github.zigcat.greenhub.auth_provider.exceptions;

public class ServerErrorException extends CoreException {
    public ServerErrorException(String message, int code) {
        super(message, code);
    }
}
