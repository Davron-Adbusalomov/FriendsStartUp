package com.example.demo.management.mapper;

import com.example.demo.management.dto.ParentDTO;
import com.example.demo.management.model.Parent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ParentMapper {

 ParentMapper INSTANCE = Mappers.getMapper(ParentMapper.class);

 Parent toModel(ParentDTO parentDTO);

 ParentDTO toDTO(Parent parent);

}
