package com.example.demo.exam.controller;

import com.example.demo.exam.dto.WrittenQuestionsEvaluateDTO;
import com.example.demo.exam.service.QuizResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("api/v1/quiz_results")
@RequiredArgsConstructor
public class QuizResultController {

    private final QuizResultService quizResultsService;

//    @Operation(
//            summary = "Getting written questions for a quiz",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Success"),
//                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
//                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
//                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
//                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
//            })
//    @PreAuthorize("hasAnyAuthority('GET_WRITTEN_QUESTIONS')")
//    @GetMapping("getWrittenQuestions/{groupName}/{quizId}")
//    public ResponseEntity<?> getWrittenQuestions(@PathVariable String groupName,@PathVariable UUID quizId){
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(quizResultsService.getWrittenQuestions(groupName, quizId));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @Operation(
            summary = "Recording quiz results",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('RECORD_QUIZ_RESULT')")
    @PostMapping("/recordResult")
    public ResponseEntity<?> recordResult(@RequestParam(name = "quizId") UUID quizId,
                                          @RequestParam(name = "studentId") Long studentId,
                                          @RequestBody List<WrittenQuestionsEvaluateDTO> writtenQuestionsEvaluateDTO){
        try {
            quizResultsService.assignQuizResult(studentId, quizId, writtenQuestionsEvaluateDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully recorded!");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Finalizing a quiz",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('FINALIZE_QUIZ')")
    @PostMapping("/finalize/{quizId}")
    public ResponseEntity<?> finalizeQuiz(@PathVariable UUID quizId) throws TelegramApiException {
        quizResultsService.finalizeQuiz(quizId);
        return ResponseEntity.status(HttpStatus.OK).body("finalized successfully!");
    }

    @Operation(
            summary = "Finalizing a quiz",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('GET_RANKINGS')")
    @PostMapping("")
    public ResponseEntity<?> getRankings(
            @RequestParam(name = "quizId", required = false) UUID quizId,
            @RequestParam(name = "groupingId", required = false) UUID groupingId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(quizResultsService.getRankings(groupingId, quizId));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
