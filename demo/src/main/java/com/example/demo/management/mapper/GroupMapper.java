package com.example.demo.management.mapper;

import com.example.demo.exam.mapper.QuizMapper;
import com.example.demo.exam.model.Quiz;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.model.Grouping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "teacherName", expression = "java(getTeacherName(grouping))")
    @Mapping(target = "teacherId", expression = "java(getTeacherId(grouping))")
    @Mapping(target = "quizzes", expression = "java(getQuizIds(grouping))")
    GroupDTO toDto(Grouping grouping);

    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Grouping toEntity(GroupDTO dto);

    List<Grouping> toEntity(List<GroupDTO> dtos);

    default String getTeacherName(Grouping grouping) {
        return grouping.getTeacher() != null ? grouping.getTeacher().getName() : null;
    }

    default Long getTeacherId(Grouping grouping) {
        return grouping.getTeacher() != null ? grouping.getTeacher().getId() : null;
    }

    default List<Long> getQuizIds(Grouping grouping) {
        if (grouping.getQuizzes() == null) return new ArrayList<>();
        return grouping.getQuizzes().stream().map(Quiz::getId).toList();
    }
}


