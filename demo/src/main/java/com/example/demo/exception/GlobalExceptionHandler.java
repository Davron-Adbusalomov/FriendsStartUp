package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleBadCredentials(BadCredentialsException ex) {

        String message = switch (ex.getMessage()) {
            case "user_not_found" -> "user_not_found";
            case "incorrect_password" -> "incorrect_password";
            default -> "invalid_credentials";
        };

        return Map.of(
                "status", 401,
                "message", message
        );
    }
}

