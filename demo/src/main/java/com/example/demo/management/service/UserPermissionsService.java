package com.example.demo.management.service;

import com.example.demo.enums.PermissionEnum;
import com.example.demo.management.dto.request.SaveUserPermissionsDto;
import com.example.demo.management.dto.response.UserPermissionsDto;
import com.example.demo.management.mapper.UserPermissionsMapper;
import com.example.demo.management.model.rbac.UserPermissionEntity;
import com.example.demo.management.repository.DefaultPermissionsRepository;
import com.example.demo.management.repository.RoleRepository;
import com.example.demo.management.repository.UserPermissionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPermissionsService {

    private final UserPermissionsRepository userPermissionsRepository;
    private final UserPermissionsMapper userPermissionsMapper;
    private final RoleRepository roleRepository;

    public List<String> findByUserId(long userId) {
        return userPermissionsRepository.findByUserId(userId).stream()
                .map(userPermissionsMapper::toDto)
                .map(dto -> dto.getPermission().name())
                .toList();
    }

    public void updateUserPermissions(SaveUserPermissionsDto request) {
        List<String> userPermissions = findByUserId(request.getUserId());

        Set<String> newPermissions = new HashSet<>(request.getPermissions());
        Set<String> existingPermissions = new HashSet<>(userPermissions);

        // Permissions to add
        List<String> permissionsToAdd = newPermissions.stream()
                .filter(permission -> !existingPermissions.contains(permission))
                .toList();

        // Permissions to remove
        List<String> permissionsToRemove = existingPermissions.stream()
                .filter(permission -> !newPermissions.contains(permission))
                .toList();

        // Add new permissions
        for (String permission : permissionsToAdd) {
            UserPermissionEntity entity = new UserPermissionEntity();
            entity.setUserId(request.getUserId());
            entity.setName(PermissionEnum.valueOf(permission));
            userPermissionsRepository.save(entity);
        }

        // Remove old permissions
        for (String permission : permissionsToRemove) {
            userPermissionsRepository.deleteByUserIdAndName(request.getUserId(), PermissionEnum.valueOf(permission));
        }
    }

    public List<String> findPermissionsByRoleId(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"))
                .getDefaultPermissions()
                .stream()
                .map(permission -> permission.getName().name())
                .collect(Collectors.toList());
    }

}
