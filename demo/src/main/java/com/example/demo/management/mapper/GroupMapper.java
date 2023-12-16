package com.example.demo.management.mapper;

import com.example.demo.management.dto.GroupDTO;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.management.mapper.StudentMapper.INSTANCE;

@Mapper
public interface GroupMapper {
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    GroupDTO toDTO(Grouping grouping);

    ArrayList<GroupDTO> toDTO(ArrayList<Grouping> groupings);

    Grouping toModel(GroupDTO groupDTO);

}
