package com.example.demo.management.mapper;

import com.example.demo.management.dto.RoleDto;
import com.example.demo.management.model.rbac.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RoleMapper {

    @Mapping(target = "title", ignore = true)
    RoleDto toDto(RoleEntity role);

    RoleEntity toEntity(RoleDto dto);
}
