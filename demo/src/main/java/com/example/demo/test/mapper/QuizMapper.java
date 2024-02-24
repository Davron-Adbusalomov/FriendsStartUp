package com.example.demo.test.mapper;

import com.example.demo.test.dto.QuizDTO;
import com.example.demo.test.model.Quiz;
import org.mapstruct.Mapper;

@Mapper
public interface QuizMapper {
    static QuizDTO toDTO(Quiz quiz){
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(quiz.getId());
        quizDTO.setDuration(quiz.getDuration());
        quizDTO.setQuestions_num(quiz.getQuestions_num());
        quizDTO.setQuestions(quiz.getQuestions());
        quizDTO.setGroupingId(quiz.getGrouping().getId());
        quizDTO.setTeacherId(quiz.getTeacher().getId());
        quizDTO.setStartTime(quiz.getStartTime());
       // quizDTO.setQuizResult(quiz.getQuizResult());
        return quizDTO;
    }
//    static Quiz toModel(QuizDTO quizDTO){
//        Quiz quiz = new Quiz();
//        quiz.setId(quizDTO.getId());
//        quiz.setDuration(quizDTO.getDuration());
//        quiz.setQuestions_num(quizDTO.getQuestions_num());
//        quiz.setQuizResult(quizDTO.getQuizResult());
//        quiz.setTeacher(quizDTO.getTeacherId());
//        quiz.setGrouping(quizDTO.getGroupingId());
//        qu
//    }
}
