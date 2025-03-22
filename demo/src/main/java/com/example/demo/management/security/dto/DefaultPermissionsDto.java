package com.example.demo.management.security.dto;

import com.example.demo.enums.PermissionEnum;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DefaultPermissionsDto {
    private PermissionEnum name;
    private String description;
}
