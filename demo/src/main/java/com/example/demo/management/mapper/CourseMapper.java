package com.example.demo.management.mapper;

import com.example.demo.management.dto.CourseDTO;
import com.example.demo.management.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "imageFile", ignore = true)
    CourseDTO toDto(Course course);

    Course toEntity(CourseDTO dto);
}
