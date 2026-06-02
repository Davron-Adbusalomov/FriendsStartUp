package com.example.demo.management.mapper;

import com.example.demo.management.dto.CourseDTO;
import com.example.demo.management.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "imageFile", ignore = true)
    @Mapping(target = "teacherName", expression = "java(course.getTeacher() != null ? course.getTeacher().getFullName() : null)")
    CourseDTO toDto(Course course);

    @Mapping(target = "teacher", ignore = true)
    Course toEntity(CourseDTO dto);
}
