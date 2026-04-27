package com.example.demo.management.mapper;

import com.example.demo.exam.model.Quiz;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.dto.TeacherSummaryDTO;
import com.example.demo.management.model.Grouping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "teacher", expression = "java(this.getTeacher(grouping))")
    @Mapping(target = "quizzes", expression = "java(this.getQuizIds(grouping))")
    @Mapping(target = "students", expression = "java(this.getStudents(grouping))")
    @Mapping(target = "isStudentAccessible", ignore = true)
    @Mapping(target = "progressPercentage", expression = "java(lessonProgressService.calculateProgressPercentage(grouping.getId()))")
    GroupDTO toDto(Grouping grouping);

    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Grouping toEntity(GroupDTO dto);

    List<Grouping> toEntity(List<GroupDTO> dtos);

    default TeacherSummaryDTO getTeacher(Grouping grouping) {
        if (grouping.getTeacher() == null) return null;
        TeacherSummaryDTO teacherSummaryDTO = new TeacherSummaryDTO();
        teacherSummaryDTO.setId(grouping.getTeacher().getId());
        teacherSummaryDTO.setFullName(grouping.getTeacher().getFullName());
        teacherSummaryDTO.setImage(grouping.getTeacher().getImage());
        teacherSummaryDTO.setTitle(grouping.getTeacher().getTitle());
        return teacherSummaryDTO;
    }

    default List<UUID> getQuizIds(Grouping grouping) {
        if (grouping.getQuizzes() == null) return new ArrayList<>();
        return grouping.getQuizzes().stream().map(Quiz::getId).toList();
    }

    default List<StudentDTO> getStudents(Grouping grouping) {
        if (grouping.getStudents() == null) return new ArrayList<>();
        return grouping.getStudents().stream().map(student -> {
            StudentDTO dto = new StudentDTO();
            dto.setId(student.getId());
            dto.setFullName(student.getFullName());
            dto.setEmail(student.getEmail());
            dto.setImage(student.getImage());
            dto.setPhoneNumber(student.getPhoneNumber());
            dto.setParentContact(student.getParentContact());
            dto.setGroupNames(student.getGroupings().stream().map(Grouping::getName).toList());
            return dto;
        }).toList();
    }
}


