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


import java.util.ArrayList;
import java.util.List;
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


    public void save(SaveUserPermissionsDto userPermissionsDto) {
        List<UserPermissionEntity> entities = new ArrayList<>();
        for (String per:userPermissionsDto.getPermissions()) {
            UserPermissionEntity entity = new UserPermissionEntity();
            entity.setUserId(userPermissionsDto.getUserId());
            entity.setName(PermissionEnum.valueOf(per));
            entity.updateEntity();
            entities.add(entity);
        }
        userPermissionsRepository.saveAll(entities);
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
