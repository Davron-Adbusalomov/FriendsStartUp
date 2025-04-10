package com.example.demo.management.mapper;

import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.model.Grouping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupMapper {

    @Mapping(target = "teacherName", expression = "java(getTeacherName(grouping))")
    GroupDTO toDto(Grouping grouping);

    List<GroupDTO> toDto(List<Grouping> groupings);

    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "students", ignore = true)
    Grouping toEntity(GroupDTO groupDTO);

    default String getTeacherName(Grouping grouping) {
        if (grouping.getTeacher() != null) return grouping.getTeacher().getName();
        return null;
    }
}
