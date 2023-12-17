package com.example.demo.test.mapper;

import com.example.demo.test.dto.QuestionDTO;
import com.example.demo.test.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface QuestionMapper {

        QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

        QuestionDTO toDTO(Question question);

        ArrayList<QuestionDTO> toDTO(ArrayList<Question> questions);

        Question toModel(QuestionDTO questionDTO);


}
