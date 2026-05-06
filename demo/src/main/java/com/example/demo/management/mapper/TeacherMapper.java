package com.example.demo.management.mapper;

import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.dto.SubjectDTO;
import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.dto.TeacherInfoDTO;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "groupList", expression = "java(getGroupDto(teacher))")
    @Mapping(target = "subject", expression = "java(getSubjectDto(teacher))")
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
                dto.setSubjectId(g.getSubjectId());
                dto.setTeacherId(teacher.getId());
                dto.setTime(g.getTime());
                groupDTOs.add(dto);
            }
        }
        return groupDTOs;
    }

    default SubjectDTO getSubjectDto(Teacher teacher) {
        if (teacher.getSubject() != null) {
            SubjectDTO subjectDTO = new SubjectDTO();
            subjectDTO.setId(teacher.getSubject().getId());
            subjectDTO.setName(teacher.getSubject().getName());
            subjectDTO.setDescription(teacher.getSubject().getDescription());
            subjectDTO.setCode(teacher.getSubject().getCode());
            return subjectDTO;
        }
        return null;
    }

    @Mapping(target = "subject", expression = "java(getSubjectDto(teacher))")
    TeacherInfoDTO toInfoDto(Teacher teacher);
}

