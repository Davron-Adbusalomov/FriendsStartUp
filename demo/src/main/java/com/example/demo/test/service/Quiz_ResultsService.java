package com.example.demo.test.service;

import com.example.demo.management.repository.StudentRepository;
import com.example.demo.test.dto.CheckingQuizDTO;
import com.example.demo.test.dto.Quiz_ResultsDTO;
import com.example.demo.test.model.Quiz;
import com.example.demo.test.model.Quiz_Results;
import com.example.demo.test.repository.QuizRepository;
import com.example.demo.test.repository.Quiz_ResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Quiz_ResultsService {
    @Autowired
    private Quiz_ResultsRepository quizResultsRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Quiz_ResultsDTO getQuizResult(){
        return null;
    }

    public void assignQuizResult(CheckingQuizDTO checkingQuizDTO, Long quizId){
        Optional<Quiz> quiz = quizRepository.findById(quizId);

        Quiz_Results quizResults = new Quiz_Results();
        quizResults.setMark(checkingQuizDTO.getCurrent_mark());
        quizResults.assignQuiz(quiz.get());
        quizResults.assignStudent(studentRepository.findById(checkingQuizDTO.getStudent_id()).get());

        quizResultsRepository.save(quizResults);
    }
}
