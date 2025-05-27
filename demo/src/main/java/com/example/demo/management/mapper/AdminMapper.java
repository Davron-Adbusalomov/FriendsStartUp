package com.example.demo.management.mapper;

import com.example.demo.management.dto.AdminDTO;
import com.example.demo.management.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminMapper {
    AdminDTO toDTO(Admin admin);

    Admin toModel(AdminDTO adminDTO);

}
