package com.example.demo.test.controller;

import com.example.demo.test.dto.CheckingQuizDTO;
import com.example.demo.test.service.QuizResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
@RequestMapping("api/quiz_results")
public class QuizResultController {

    @Autowired
    private QuizResultService quizResultsService;

    @PostMapping("recordingResult/{quizId}")
    public ResponseEntity<?> recordResult(@RequestBody CheckingQuizDTO checkingQuizDTO, @PathVariable Long quizId){
        try {
            quizResultsService.assignQuizResult(checkingQuizDTO, quizId);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully recorded!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("finalizingQuiz/{quizId}")
    public ResponseEntity<?> finalizeQuiz(@PathVariable Long quizId) throws TelegramApiException {
        quizResultsService.finalizeQuiz(quizId);
        return ResponseEntity.status(HttpStatus.OK).body("finalized successfully!");
    }

}
