package com.example.demo.management.mapper;

import com.example.demo.management.dto.LessonDTO;
import com.example.demo.management.model.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonDTO toDto(Lesson lesson);

    @Mapping(target = "grouping", ignore = true)
    @Mapping(target = "center", ignore = true)
    Lesson toEntity(LessonDTO dto);
}
