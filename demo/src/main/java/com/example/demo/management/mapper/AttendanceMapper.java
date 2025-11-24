package com.example.demo.management.mapper;

import com.example.demo.management.dto.AttendanceDto;
import com.example.demo.management.model.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    AttendanceDto toDto(Object attendance);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "grouping", ignore = true)
    Attendance toEntity(AttendanceDto dto);
}
