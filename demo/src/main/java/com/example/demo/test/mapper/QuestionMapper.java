package com.example.demo.test.mapper;

import com.example.demo.test.dto.QuestionDTO;
import com.example.demo.test.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface QuestionMapper {

        QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

        static QuestionDTO toDTO(Question question){
                QuestionDTO questionDTO = new QuestionDTO();
                questionDTO.setId(question.getId());
                questionDTO.setMark(question.getMark());
                questionDTO.setLevel(question.getLevel());
                questionDTO.setTitle(question.getTitle());
                questionDTO.setType(question.getType());
                questionDTO.setSubject(question.getSubject());
                questionDTO.setImage(question.getImage());
                questionDTO.setRight_answer(question.getRight_answer());
                questionDTO.setTeacherId(question.getTeacher().getId());
               // questionDTO.setOptions(question.getOptions());
                return questionDTO;
        }

        ArrayList<QuestionDTO> toDTO(ArrayList<Question> questions);

//        static Question toModel(QuestionDTO questionDTO){
//                Question question = new Question();
//                question.setId(questionDTO.getId());
//                question.setGroup_name(questionDTO.getGroup_name());
//                question.setTitle(questionDTO.getTitle());
//                question.setType(questionDTO.getType());
//                question.setMark(questionDTO.getMark());
//                question.setRight_answer(questionDTO.getRight_answer());
//                question.setTeacher(questionDTO.getTeacher());
//                return question;
//        };


}
