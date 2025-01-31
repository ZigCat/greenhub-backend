package com.github.zigcat.greenhub.user_provider.exceptions;

import com.github.zigcat.greenhub.user_provider.exceptions.base.GreenhubException;
import lombok.Getter;

@Getter
public class ForbiddenException extends GreenhubException {
    public ForbiddenException(String message) {
        super(message, 403);
    }
}
