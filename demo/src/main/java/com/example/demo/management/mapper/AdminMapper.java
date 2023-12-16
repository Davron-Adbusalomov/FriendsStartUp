package com.example.demo.management.mapper;

import com.example.demo.management.dto.AdminDTO;
import com.example.demo.management.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapper {
    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

    AdminDTO toDTO(Admin admin);

    Admin toModel(AdminDTO adminDTO);
}
