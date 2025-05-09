package com.example.demo.management.service;

import com.example.demo.management.dto.response.UserPermissionsDto;
import com.example.demo.management.mapper.UserPermissionsMapper;
import com.example.demo.management.model.rbac.UserPermissionEntity;
import com.example.demo.management.repository.UserPermissionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPermissionsService {

    private final UserPermissionsRepository userPermissionsRepository;
    private final UserPermissionsMapper userPermissionsMapper;

    public List<UserPermissionsDto> findByUserId(long userId) {
        List<UserPermissionEntity> optionalUserPermissions = userPermissionsRepository.findByUserId(userId);
        List<UserPermissionsDto> userPermissionsDtos = new ArrayList<>();
        for (UserPermissionEntity entity : optionalUserPermissions) {
            userPermissionsDtos.add(userPermissionsMapper.toDto(entity));
        }
        return userPermissionsDtos;
    }

//    public void save(UserPermissionsDto)

}
