package com.example.demo.management.mapper;

import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "groupList", expression = "java(getGroupDto(teacher))")
    TeacherDTO toDto(Teacher teacher);

    @Mapping(target = "groupList", ignore = true)
    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Teacher toEntity(TeacherDTO teacherDto);

    default List<GroupDTO> getGroupDto(Teacher teacher) {
        List<GroupDTO> groupDTOs = new ArrayList<>();
        if (teacher.getGroupList() != null) {
            for (Grouping g : teacher.getGroupList()) {
                GroupDTO dto = new GroupDTO();
                dto.setId(g.getId());
                dto.setName(g.getName());
                dto.setSubject(g.getSubject());
                groupDTOs.add(dto);
            }
        }
        return groupDTOs;
    }
}

