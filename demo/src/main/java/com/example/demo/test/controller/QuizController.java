package com.example.demo.test.controller;

import com.example.demo.test.dto.QuizDTO;
import com.example.demo.test.dto.QuizDTOForRequest;
import com.example.demo.test.model.Response;
import com.example.demo.test.service.QuizService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("createQuiz")
    public ResponseEntity<?> createQuiz(@RequestBody QuizDTOForRequest quizDTO){
        return quizService.createQuiz(quizDTO);
    }


    @GetMapping("beginQuiz/{quizId}")
    public ResponseEntity<?> beginQuiz(@PathVariable Long quizId) {
        try {
            QuizDTO quiz = quizService.beginQuiz(quizId);
            return ResponseEntity.status(HttpStatus.OK).body(quiz);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("checkMultipleChoice/{studentId}/{quizId}")
    public ResponseEntity<?> checkMultipleChoice(@RequestBody List<Response> responseList, @PathVariable Long studentId, @PathVariable Long quizId){
        try {
            String checkingQuizDTO =quizService.checkingMultipleChoiceQuestions(responseList, studentId, quizId);
            return ResponseEntity.status(HttpStatus.OK).body(checkingQuizDTO);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
