package com.example.demo.management.mapper;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "groupNames", expression = "java(getGroupNames(student))")
    @Mapping(target = "parentContact", source = "parentContact")
    @Mapping(target = "parentChatId", source = "parentChatId")
    StudentDTO toDto(Student student);

    List<StudentDTO> toDto(List<Student> students);

    @Mapping(target = "groupings", ignore = true)
    @Mapping(target = "quizResults", ignore = true)
    @Mapping(target = "parentContact", source = "parentContact")
    @Mapping(target = "parentChatId", source = "parentChatId")
    Student toEntity(StudentDTO studentDto);

    default List<String> getGroupNames(Student student) {
        if (student.getGroupings() == null || student.getGroupings().isEmpty()) {
            return List.of();
        }
        return student.getGroupings()
                .stream()
                .map(Grouping::getName)
                .collect(Collectors.toList());
    }


}
