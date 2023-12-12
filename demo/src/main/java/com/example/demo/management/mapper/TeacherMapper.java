package com.example.demo.management.mapper;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface TeacherMapper {
    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);

    TeacherDTO toDTO(Teacher teacher);

    ArrayList<TeacherDTO> toDTO(ArrayList<Teacher> teachers);

    Teacher toModel(TeacherDTO teacherDTO);
}
