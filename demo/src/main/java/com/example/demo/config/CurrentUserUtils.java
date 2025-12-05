package com.example.demo.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.example.demo.management.authentication.dto.AuthenticationDetailsDto;

import java.util.UUID;

public class CurrentUserUtils {

    public static AuthenticationDetailsDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticationDetailsDto) {
            return (AuthenticationDetailsDto) auth.getPrincipal();
        }
        return null;
    }

    public static Long getUserId() {
        AuthenticationDetailsDto user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public static UUID getCenterId() {
        AuthenticationDetailsDto user = getCurrentUser();
        return user != null ? user.getCenterId() : null;
    }

    public static String getUsername() {
        AuthenticationDetailsDto user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }
}
