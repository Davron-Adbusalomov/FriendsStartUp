package com.example.demo.management.mapper;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.GroupRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    static StudentDTO toDTO(Student student){
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setNumber(student.getNumber());
        studentDTO.setName(student.getName());
        studentDTO.setRole(student.getRole());
//        List<String> groupList = new ArrayList<>();
//        groupList.add(student.getGroupings().get(1).getName());
        studentDTO.setGroupList(student.getGroupings());
        studentDTO.setParent_contact(student.getParent_contact());
        studentDTO.setParent_chatId(student.getParent_chatId());
        studentDTO.setUsername(student.getUsername());
        studentDTO.setPassword(student.getPassword());
        return studentDTO;
    };

    static ArrayList<StudentDTO> toDTO(List<Student> lessons) {
        ArrayList<StudentDTO> lessonDTOs = new ArrayList<>();
        for (Student lesson : lessons) {
            lessonDTOs.add(toDTO(lesson));
        }
        return lessonDTOs;
    }

    static Student toModel(StudentDTO studentDTO){
        Student student = new Student();
        student.setNumber(studentDTO.getNumber());
        student.setName(studentDTO.getName());
        student.setRole(studentDTO.getRole());
        student.setParent_contact(studentDTO.getParent_contact());
        student.setParent_chatId(studentDTO.getParent_chatId());
        student.setUsername(studentDTO.getUsername());
        student.setPassword(studentDTO.getPassword());
        return student;
    }
}
