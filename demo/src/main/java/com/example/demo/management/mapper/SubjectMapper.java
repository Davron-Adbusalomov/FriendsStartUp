package com.example.demo.management.mapper;

import com.example.demo.management.dto.SubjectDTO;
import com.example.demo.management.model.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    SubjectDTO toDto(Subject subject);

    Subject toEntity(SubjectDTO subjectDTO);
}
