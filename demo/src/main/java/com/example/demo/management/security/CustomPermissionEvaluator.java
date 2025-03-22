package com.example.demo.management.security;

import com.example.demo.management.authentication.dto.AuthenticationDetailsDto;
import com.example.demo.management.model.rbac.UserPermissionEntity;
import com.example.demo.management.repository.UserPermissionsRepository;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.io.Serializable;
import java.util.List;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final UserPermissionsRepository userPermissionsRepository;

    public CustomPermissionEvaluator(UserPermissionsRepository userPermissionsRepository) {
        this.userPermissionsRepository = userPermissionsRepository;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return false;
        }

        AuthenticationDetailsDto userDetails = (AuthenticationDetailsDto) authentication.getPrincipal();
        Long userId = userDetails.getId();

        // Example: Check if the user has the required permission
        return userHasPermission(userId, targetDomainObject.toString(), permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false; // Not needed for most cases
    }

    private boolean userHasPermission(Long userId, String resource, String action) {
        List<UserPermissionEntity> userPermissions = userPermissionsRepository.findByUserId(userId);

        List<String> permissions = userPermissions.stream().map(item -> item.getName().name()).toList();
        return permissions.contains(resource + "_" + action);
    }
}
