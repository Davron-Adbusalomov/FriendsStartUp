package com.example.demo.management.mapper;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    StudentDTO toDTO(Student student);

    ArrayList<StudentDTO> toDTO(ArrayList<Student> students);

    Student toModel(StudentDTO studentDTO);
    }
