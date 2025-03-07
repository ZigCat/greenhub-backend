package com.github.zigcat.greenhub.auth_provider.presentation.controllers;

import com.github.zigcat.greenhub.auth_provider.exceptions.CoreException;
import com.github.zigcat.greenhub.auth_provider.presentation.PresentationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerErrorHandler {
    @ExceptionHandler(CoreException.class)
    public ResponseEntity<Object> handleRuntimeException(CoreException e){
        PresentationDTO.ApiError error = new PresentationDTO.ApiError(e.getMessage(), e.getCode());
        return new ResponseEntity<>(error, HttpStatus.valueOf(e.getCode()));
    }
}
