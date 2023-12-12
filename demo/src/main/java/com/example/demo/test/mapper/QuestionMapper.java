package com.example.demo.test.mapper;

import com.example.demo.test.dto.AnswerDTO;
import com.example.demo.test.dto.QuestionDTO;
import com.example.demo.test.model.Answer;
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

        default Question toModel(QuestionDTO questionDTO, @MappingTarget Question question) {
            question.setQuestion_id(questionDTO.getQuestion_id());
            question.setQuestion(questionDTO.getQuestion());
            question.setAnswers(mapAnswerDTOsToAnswers(questionDTO.getAnswers()));
            return question;
        }

        default List<Answer> mapAnswerDTOsToAnswers(List<AnswerDTO> answerDTOs) {
            List<Answer> answers = new ArrayList<>();
            for (AnswerDTO answerDTO : answerDTOs) {
                Answer answer = new Answer();
                answers.add(answer);
            }
            return answers;
        }

}
