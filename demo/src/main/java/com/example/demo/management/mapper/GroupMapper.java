package com.example.demo.management.mapper;

import com.example.demo.management.dto.GroupDTO;

import com.example.demo.management.model.Grouping;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface GroupMapper {
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    GroupDTO toDTO(Grouping grouping);

    ArrayList<GroupDTO> toDTO(ArrayList<Grouping> groupings);

    Grouping toModel(GroupDTO groupDTO);

    public static Grouping toModel(GroupDTO groupDTO, Grouping grouping) {
//        grouping.setId(groupDTO.getId());
//        grouping.setName(groupDTO.getName());
//        grouping.setSubject(groupDTO.getSubject());
//
//        grouping.setTeacher(TeacherMapper.INSTANCE.toModel(groupDTO.getTeacherDTO()));
//
//        List<Student> students = groupDTO.getStudentDTOList().stream()
//                .map(StudentMapper.INSTANCE::toModel)
//                .collect(Collectors.toList());
//        grouping.setStudents(students);

        return grouping;
    }
}
