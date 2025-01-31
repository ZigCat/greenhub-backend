package com.github.zigcat.greenhub.user_provider.controllers;

import com.github.zigcat.greenhub.user_provider.dto.rest.messages.ApiError;
import com.github.zigcat.greenhub.user_provider.exceptions.ForbiddenException;
import com.github.zigcat.greenhub.user_provider.exceptions.NotFoundException;
import com.github.zigcat.greenhub.user_provider.exceptions.base.GreenhubException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerErrorHandler {
    @ExceptionHandler(GreenhubException.class)
    public ResponseEntity<Object> handleRuntimeException(GreenhubException e){
        ApiError error = new ApiError(e.getCode(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.valueOf(e.getCode()));
    }
}
