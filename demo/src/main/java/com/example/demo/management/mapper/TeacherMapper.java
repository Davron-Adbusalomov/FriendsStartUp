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

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherMapper {

    @Mapping(target = "groupList", expression = "java(getGroupDto(teacher))")
    TeacherDTO toDto(Teacher teacher);

    List<TeacherDTO> toDto(List<Teacher> teachers);

    @Mapping(target = "groupList", source = "groupList")
    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Teacher toEntity(TeacherDTO teacherDto);

    default List<GroupDTO> getGroupDto(Teacher teacher) {
        List<GroupDTO> groupDTOs = new ArrayList<>();
        if (teacher.getGroupList() != null && !teacher.getGroupList().isEmpty()) {
            for (Grouping g : teacher.getGroupList()) {
                GroupDTO groupDTO = new GroupDTO();
                groupDTO.setId(g.getId());
                groupDTO.setName(g.getName());
                groupDTO.setSubject(g.getSubject());
                groupDTOs.add(groupDTO);
            }
        }
        return groupDTOs;
    }
}
