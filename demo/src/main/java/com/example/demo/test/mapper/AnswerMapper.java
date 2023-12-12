package com.example.demo.test.mapper;

import com.example.demo.test.dto.AnswerDTO;
import com.example.demo.test.model.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    AnswerDTO toDTO(Answer answer);

    ArrayList<AnswerDTO> toDTO(ArrayList<Answer> answers);

    Answer toModel(AnswerDTO answerDTO);

    default Answer toModel(AnswerDTO answerDTO, Answer answer) {
        answer.setAnswer(answerDTO.getAnswer());
        answer.setId(answerDTO.getId());
        answer.setCorrect(answerDTO.isCorrect());
        return answer;
    }
}
