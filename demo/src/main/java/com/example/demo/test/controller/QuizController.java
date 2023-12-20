package com.example.demo.test.controller;

import com.example.demo.test.dto.QuizDTO;
import com.example.demo.test.mapper.QuizMapper;
import com.example.demo.test.model.Quiz;
import com.example.demo.test.service.QuizService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("createQuiz")
    public ResponseEntity<?> createQuiz(@RequestBody QuizDTO quizDTO){
        return quizService.createQuiz(quizDTO);
    }


    @GetMapping("beginQuiz/{quizId}")
    public ResponseEntity<?> beginQuiz(@PathVariable Long quizId) {
        try {
            Quiz quiz = quizService.beginQuiz(quizId);
            return ResponseEntity.status(HttpStatus.OK).body(QuizMapper.toDTO(quiz));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
