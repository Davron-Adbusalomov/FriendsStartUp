package com.example.demo.management.mapper;

import com.example.demo.management.dto.BadgeDTO;
import com.example.demo.management.dto.request.BadgeRequestDto;
import com.example.demo.management.model.Badge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BadgeMapper {

    @Mapping(target = "active", ignore = true)
    BadgeDTO toDto(Badge badge);

    @Mapping(target = "students", ignore = true)
    @Mapping(target = "center", ignore = true)
    Badge toEntity(BadgeDTO dto);

    @Mapping(target = "students", ignore = true)
    @Mapping(target = "center", ignore = true)
    Badge toEntity(BadgeRequestDto dto);
}
