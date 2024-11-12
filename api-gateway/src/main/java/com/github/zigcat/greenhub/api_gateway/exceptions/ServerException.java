package com.github.zigcat.greenhub.api_gateway.exceptions;

public class ServerException extends RuntimeException {
    public ServerException() {
        super("500: Internal server error");
    }

    public ServerException(String message) {
        super("500: " + message);
    }
}
