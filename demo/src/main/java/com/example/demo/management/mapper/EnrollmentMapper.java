package com.example.demo.management.mapper;

import com.example.demo.management.dto.EnrollmentDTO;
import com.example.demo.management.model.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    @Mapping(target = "grouping.quizzes", ignore = true)
    @Mapping(target = "grouping.students", ignore = true)
    EnrollmentDTO toDTO(Enrollment enrollment);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "grouping", ignore = true)
    Enrollment toEntity(EnrollmentDTO enrollmentDTO);
}
