package com.example.demo.management.mapper;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TeacherMapper {

    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);

    static TeacherDTO toDTO(Teacher teacher){
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(teacher.getId());
        teacherDTO.setName(teacher.getName());
        teacherDTO.setImage(teacher.getImage());
        teacherDTO.setExperience(teacher.getExperience());
        teacherDTO.setPhone_num(teacher.getPhone_num());
        teacherDTO.setUsername(teacher.getUsername());
        teacherDTO.setSubject(teacher.getSubject());
        teacherDTO.setRole(teacher.getRole());
        teacherDTO.setGroupList(teacher.getGroupings());
        return teacherDTO;
    };

    static ArrayList<TeacherDTO> toDTO(List<Teacher> lessons) {
        ArrayList<TeacherDTO> teacherDTOS = new ArrayList<>();
        for (Teacher teacher : lessons) {
            teacherDTOS.add(toDTO(teacher));
        }
        return teacherDTOS;
    }

    Teacher toModel(TeacherDTO teacherDTO);
}
