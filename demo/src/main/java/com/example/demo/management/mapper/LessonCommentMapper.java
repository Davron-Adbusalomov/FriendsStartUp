package com.example.demo.management.mapper;

import com.example.demo.management.dto.LessonCommentDTO;
import com.example.demo.management.model.LessonComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonCommentMapper {
    @Mapping(target = "author", source = "user.fullName")
    LessonCommentDTO toDto(LessonComment comment);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    LessonComment toEntity(LessonCommentDTO dto);
}
