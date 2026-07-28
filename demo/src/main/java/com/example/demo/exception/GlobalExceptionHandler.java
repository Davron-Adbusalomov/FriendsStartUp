package com.example.demo.exception;

import com.example.demo.config.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
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

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 401);
        body.put("message", message);
        body.put("centerId", TenantContext.getCenterId());
        return body;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(
            EntityNotFoundException ex
    ) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 404);
        body.put("message", ex.getMessage());
        body.put("centerId", TenantContext.getCenterId());
        return body;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleAccessDenied(
            AccessDeniedException ex
    ) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 403);
        body.put("message", "Access denied");
        body.put("centerId", TenantContext.getCenterId());
        return body;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleAllExceptions(
            Exception ex
    ) {
        ex.printStackTrace();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 500);
        body.put("message", ex.getMessage());
        body.put("centerId", TenantContext.getCenterId());
        return body;
    }
}