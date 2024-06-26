package com.charlesxvr.portfoliobackend.security.controllers;

import com.charlesxvr.portfoliobackend.exceptions.ErrorMessage;
import com.charlesxvr.portfoliobackend.exceptions.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class TokenControllerAdvice {
    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.FORBIDDEN.value());
    }
}
