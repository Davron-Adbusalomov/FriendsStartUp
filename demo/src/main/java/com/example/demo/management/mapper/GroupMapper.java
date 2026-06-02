package com.example.demo.management.mapper;

import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.dto.TeacherSummaryDTO;
import com.example.demo.management.model.Grouping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "teacher", expression = "java(this.getTeacher(grouping))")
    @Mapping(target = "students", expression = "java(this.getStudents(grouping))")
    @Mapping(target = "isStudentAccessible", ignore = true)
    @Mapping(target = "progressPercentage", ignore = true)
    @Mapping(target = "imageFile", ignore = true)
    GroupDTO toDto(Grouping grouping);

    @Mapping(target = "students", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    Grouping toEntity(GroupDTO dto);

    List<Grouping> toEntity(List<GroupDTO> dtos);

    default TeacherSummaryDTO getTeacher(Grouping grouping) {
        if (grouping.getTeacher() == null) return null;
        TeacherSummaryDTO dto = new TeacherSummaryDTO();
        dto.setId(grouping.getTeacher().getId());
        dto.setFullName(grouping.getTeacher().getFullName());
        dto.setImage(grouping.getTeacher().getImage());
        dto.setTitle(grouping.getTeacher().getTitle());
        return dto;
    }

    default List<StudentDTO> getStudents(Grouping grouping) {
        if (grouping.getStudents() == null) return List.of();
        return grouping.getStudents().stream().map(student -> {
            StudentDTO dto = new StudentDTO();
            dto.setId(student.getId());
            dto.setFullName(student.getFullName());
            dto.setEmail(student.getEmail());
            dto.setImage(student.getImage());
            dto.setPhoneNumber(student.getPhoneNumber());
            dto.setParentContact(student.getParentContact());
            dto.setParentChatId(student.getParentChatId());
            dto.setGroupNames(student.getGroupings().stream().map(Grouping::getName).toList());
            return dto;
        }).toList();
    }
}
