package com.example.demo.management.mapper;

import com.example.demo.management.dto.LessonProgressDTO;
import com.example.demo.management.model.LessonProgress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonProgressMapper {
    LessonProgressDTO toDto(LessonProgress lessonProgress);
}
