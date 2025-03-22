package com.example.demo.management.dto;

import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.security.dto.DefaultPermissionsDto;
import lombok.Data;
import lombok.ToString;


import java.util.Set;

@Data
@ToString
//@Schema(name = "RoleDto")
public class RoleDto {
//    @Schema(name = "title", examples = {"Administrator","User"})
    private String title;
//    @Schema(name = "name", examples = {"ADMIN", "USER"})
    private RolesEnum name;
//    @Schema(name = "privilege", example = "10")
    private Integer privilege;
//    @Schema(name = "defaultPermissions", description = "Set of default permissions")
    private Set<DefaultPermissionsDto> defaultPermissions;
}
