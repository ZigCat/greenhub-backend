package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.exceptions.CoreException;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerErrorHandler {
    @ExceptionHandler(CoreException.class)
    public ResponseEntity<Object> handleRuntimeException(CoreException e){
        DTO.ApiError error = new DTO.ApiError(e.getMessage(), e.getStatusCode());
        return new ResponseEntity<>(error, HttpStatus.valueOf(e.getStatusCode()));
    }
}
