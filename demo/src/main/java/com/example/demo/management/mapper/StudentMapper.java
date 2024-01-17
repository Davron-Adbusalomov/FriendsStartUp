package com.example.demo.management.mapper;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    static StudentDTO toDTO(Student student){
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setNumber(student.getNumber());
        studentDTO.setName(student.getName());
        studentDTO.setAge(student.getAge());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setRole(student.getRole());
        studentDTO.setParent_contact(student.getParent_contact());
        studentDTO.setParent_chatId(student.getParent_chatId());
        studentDTO.setUsername(student.getUsername());
        studentDTO.setPassword(student.getPassword());
        studentDTO.setRelationship(student.getRelationship());
        return studentDTO;
    };

    ArrayList<StudentDTO> toDTO(ArrayList<Student> students);

    static Student toModel(StudentDTO studentDTO){
        Student student = new Student();
        student.setNumber(studentDTO.getNumber());
        student.setName(studentDTO.getName());
        student.setAge(studentDTO.getAge());
        student.setEmail(studentDTO.getEmail());
        student.setRole(studentDTO.getRole());
        student.setDateOfBirth(studentDTO.getDateOfBirth());
        student.setParent_contact(studentDTO.getParent_contact());
        student.setParent_chatId(studentDTO.getParent_chatId());
        student.setUsername(studentDTO.getUsername());
        student.setPassword(studentDTO.getPassword());
        student.setRelationship(studentDTO.getRelationship());
        return student;
    }
}
