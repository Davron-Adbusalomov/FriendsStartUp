package com.example.demo.management.mapper;

import com.example.demo.management.dto.response.UserPermissionsDto;
import com.example.demo.management.model.rbac.UserPermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "string", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserPermissionsMapper {

    @Mapping(target = "permission", source = "name")
    UserPermissionsDto toDto(UserPermissionEntity entity);

    @Mapping(target = "name", source = "permission")
    UserPermissionEntity toEntity(UserPermissionsDto dto);

}
