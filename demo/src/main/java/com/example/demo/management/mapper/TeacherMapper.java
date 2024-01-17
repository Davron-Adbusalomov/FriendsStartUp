package com.example.demo.management.mapper;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface TeacherMapper {

    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);

    static TeacherDTO toDTO(Teacher teacher){
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(teacher.getId());
        teacherDTO.setName(teacher.getName());
        teacherDTO.setEmail(teacher.getEmail());
        teacherDTO.setAge(teacher.getAge());
        teacherDTO.setDateOfBirth(teacher.getDateOfBirth());
        teacherDTO.setPhone_num(teacher.getPhone_num());
        teacherDTO.setUsername(teacher.getUsername());
        teacherDTO.setSubject(teacher.getSubject());
        teacherDTO.setRole(teacher.getRole());

        return teacherDTO;
    };

    ArrayList<TeacherDTO> toDTO(ArrayList<Teacher> teachers);

    Teacher toModel(TeacherDTO teacherDTO);
}
