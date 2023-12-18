package com.example.demo.test.mapper;

import com.example.demo.test.dto.OptionDTO;
import com.example.demo.test.model.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OptionMapper {
    OptionMapper INSTANCE = Mappers.getMapper(OptionMapper.class);

    // Basic mapping from Option entity to OptionDTO
    @Mapping(target = "questionId", source = "question.id")
    OptionDTO optionToOptionDTO(Option option);

    // Basic mapping from OptionDTO to Option entity
    @Mapping(target = "question.id", source = "questionId")
    Option optionDTOtoOption(OptionDTO optionDTO);

    // Mapping from Option entity to OptionDTO with explicit source for questionId
    @Mapping(target = "questionId", source = "question.id")
    OptionDTO optionToOptionDTOWithExplicitSource(Option option);

    // Mapping from OptionDTO to Option entity with explicit source for questionId
    @Mapping(target = "question.id", source = "questionId")
    Option optionDTOtoOptionWithExplicitSource(OptionDTO optionDTO);

    // Mapping from Option entity to OptionDTO with ignoring isCorrect field
    @Mapping(target = "isCorrect", ignore = true)
    OptionDTO optionToOptionDTOIgnoringIsCorrect(Option option);

    // Mapping from OptionDTO to existing Option entity with updating the entity
    @Mapping(target = "id", source = "optionDTO.id") // Assuming id is set in the DTO for updating
    @Mapping(target = "question.id", source = "optionDTO.questionId")
    void updateOptionFromDTO(OptionDTO optionDTO, @MappingTarget Option option);


}
