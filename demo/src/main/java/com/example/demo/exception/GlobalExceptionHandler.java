package com.example.demo.exception;

import com.example.demo.config.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleBadCredentials(
            BadCredentialsException ex
    ) {

        String message = switch (ex.getMessage()) {
            case "user_not_found" -> "user_not_found";
            case "incorrect_password" -> "incorrect_password";
            default -> "invalid_credentials";
        };

        return Map.of(
                "status", 401,
                "message", message,
                "centerId", TenantContext.getCenterId()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(
            EntityNotFoundException ex
    ) {

        return Map.of(
                "status", 404,
                "message", ex.getMessage(),
                "centerId", TenantContext.getCenterId()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleAccessDenied(
            AccessDeniedException ex
    ) {

        return Map.of(
                "status", 403,
                "message", "Access denied",
                "centerId", TenantContext.getCenterId()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleAllExceptions(
            Exception ex
    ) {
        ex.printStackTrace();

        return Map.of(
                "status", 500,
                "message", ex.getMessage(),
                "centerId", TenantContext.getCenterId()
        );
    }
}