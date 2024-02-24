package com.example.demo.test.controller;

import com.example.demo.test.dto.WrittenQuestionsResponseDTO;
import com.example.demo.test.service.QuizResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/quiz_results")
public class QuizResultController {

    @Autowired
    private QuizResultService quizResultsService;

    @GetMapping("getWrittenQuestions/{groupName}/{quizId}")
    public ResponseEntity<?> getWrittenQuestions(@PathVariable String groupName,@PathVariable Long quizId){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(quizResultsService.getWrittenQuestions(groupName, quizId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("recordingResult")
    public ResponseEntity<?> recordResult(@RequestBody List<WrittenQuestionsResponseDTO> writtenQuestionsResponseDTO){
        try {
            quizResultsService.assignQuizResult(writtenQuestionsResponseDTO);
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
