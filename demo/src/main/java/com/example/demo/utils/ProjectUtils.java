package com.example.demo.utils;

import com.example.demo.management.authentication.dto.AuthenticationDetailsDto;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class ProjectUtils {
    public static AuthenticationDetailsDto getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails) {
            return (AuthenticationDetailsDto) authentication.getPrincipal();
        }
        return null;
    }

}
