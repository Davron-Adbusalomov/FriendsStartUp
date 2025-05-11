package com.example.demo.management.dto.response;

import com.example.demo.enums.PermissionEnum;
import lombok.Data;

@Data
public class UserPermissionsDto {
    private Long userId;

    private PermissionEnum permission;
}
