package com.github.zigcat.greenhub.article_provider.exceptions;

public class ClientErrorException extends CoreException{
    public ClientErrorException(String message, int statusCode) {
        super(message, statusCode);
    }
}
