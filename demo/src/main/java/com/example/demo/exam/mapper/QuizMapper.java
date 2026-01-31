package com.example.demo.exam.mapper;

import com.example.demo.exam.dto.EvaluatedQuestionDetails;
import com.example.demo.exam.dto.EvaluatedQuizDetailsDTO;
import com.example.demo.exam.dto.QuizDTO;
import com.example.demo.exam.dto.QuizSummaryDTO;
import com.example.demo.exam.model.Quiz;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface QuizMapper {
    static QuizDTO toDTO(Quiz quiz){
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(quiz.getId());
        quizDTO.setDuration(quiz.getDuration());
        quizDTO.setQuestionsNum(quiz.getQuestionsNum());
        quizDTO.setQuestions(quiz.getQuestions().stream().map(QuestionMapper::toDTO).collect(java.util.stream.Collectors.toSet()));
        quizDTO.setGroupingId(quiz.getGrouping().getId());
        quizDTO.setTeacherId(quiz.getTeacher().getId());
        quizDTO.setStartTime(quiz.getStartTime());
       // quizDTO.setQuizResult(quiz.getQuizResult());
        return quizDTO;
    }

    static QuizSummaryDTO toSummaryDTO(Quiz quiz){
        QuizSummaryDTO quizSummaryDTO = new QuizSummaryDTO();
        quizSummaryDTO.setId(quiz.getId());
        quizSummaryDTO.setTitle(quiz.getTitle());
        quizSummaryDTO.setDuration(quiz.getDuration());
        quizSummaryDTO.setQuestionsNum(quiz.getQuestionsNum());
        quizSummaryDTO.setStartTime(quiz.getStartTime());
        return quizSummaryDTO;
    }

    static EvaluatedQuizDetailsDTO toEvaluatedQuizDetail(Quiz quiz){
            EvaluatedQuizDetailsDTO dto = new EvaluatedQuizDetailsDTO();
            dto.setId(quiz.getId());
            dto.setTitle(quiz.getTitle());
            dto.setDuration(quiz.getDuration());
            dto.setQuestionsNum(quiz.getQuestionsNum());
            dto.setGroupingId(quiz.getGrouping().getId());
            dto.setTeacherId(quiz.getTeacher().getId());
            dto.setCreatedAt(quiz.getCreatedAt());
            dto.setStartTime(quiz.getStartTime());
            return dto;
    }

//    static Quiz toModel(QuizDTO quizDTO){
//        Quiz quiz = new Quiz();
//        quiz.setId(quizDTO.getId());
//        quiz.setDuration(quizDTO.getDuration());
//        quiz.setQuestionsNum(quizDTO.getQuestionsNum());
//        quiz.setQuizResult(quizDTO.getQuizResult());
//        quiz.setTeacher(quizDTO.getTeacherId());
//        quiz.setGrouping(quizDTO.getGroupingId());
//        qu
//    }
}
