package com.github.zigcat.greenhub.article_provider.exceptions;

public class ServerErrorException extends CoreException{
    public ServerErrorException(String message, int statusCode) {
        super(message, statusCode);
    }
}
