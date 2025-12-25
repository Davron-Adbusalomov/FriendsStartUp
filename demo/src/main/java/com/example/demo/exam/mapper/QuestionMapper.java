package com.example.demo.exam.mapper;

import com.example.demo.exam.dto.QuestionDTO;
import com.example.demo.exam.dto.QuestionSummaryDTO;
import com.example.demo.exam.model.Option;
import com.example.demo.exam.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    static QuestionDTO toDTO(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setMark(question.getMark());
        questionDTO.setLevel(question.getLevel());
        questionDTO.setTitle(question.getTitle());
        questionDTO.setType(question.getType());
        questionDTO.setSubjectId(question.getSubjectId());
        if(question.getSubject() != null) questionDTO.setSubjectName(question.getSubject().getName());
        questionDTO.setImage(question.getImage());
//                questionDTO.setRight_answer(question.getRight_answer());
        questionDTO.setTeacherId(question.getTeacher().getId());
        if(question.getTeacher() != null) questionDTO.setTeacherName(question.getTeacher().getFullName());
        if(question.getOptions() != null) questionDTO.setOptions(question.getOptions().stream().map(Option::getText).toList());
        return questionDTO;
    }

    ArrayList<QuestionDTO> toDTO(ArrayList<Question> questions);

    static QuestionSummaryDTO toSummaryDTO(Question question) {
        QuestionSummaryDTO questionDTO = new QuestionSummaryDTO();
        questionDTO.setId(question.getId());
        questionDTO.setMark(question.getMark());
        questionDTO.setLevel(question.getLevel());
        questionDTO.setTitle(question.getTitle());
        questionDTO.setType(question.getType());
        questionDTO.setSubjectId(question.getSubjectId());
        if(question.getSubject() != null) questionDTO.setSubjectName(question.getSubject().getName());
        questionDTO.setTeacherId(question.getTeacher().getId());
        if(question.getTeacher() != null) questionDTO.setTeacherName(question.getTeacher().getFullName());
        return questionDTO;
    }


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
