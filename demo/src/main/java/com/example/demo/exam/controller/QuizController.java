package com.example.demo.exam.controller;

import com.example.demo.enums.QuizStatus;
import com.example.demo.exam.dto.QuizDTO;
import com.example.demo.exam.dto.QuizDTOForRequest;
import com.example.demo.exam.dto.QuizSummaryDTO;
import com.example.demo.exam.dto.UpcomingTaskInfo;
import com.example.demo.exam.model.Response;
import com.example.demo.exam.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("api/v1/quiz")
public class QuizController {

    private final QuizService quizService;

    @Operation(
            summary = "Creating a quiz",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('CREATE_QUIZ')")
    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestBody QuizDTOForRequest quizDTO){
        return quizService.createQuiz(quizDTO);
    }

    @Operation(
            summary = "Get quizzes list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('GET_QUIZZES_LIST')")
    @GetMapping("")
    public Page<QuizSummaryDTO> getQuizzesList(@RequestParam(name = "groupId", required = false) UUID groupId,
                                               @RequestParam(name = "title", required = false) String title,
                                               @RequestParam(name = "status", required = false) QuizStatus status,
                                               Pageable pageable){
        return quizService.getQuizzesList(groupId, title, status, pageable);
    }

    @Operation(
            summary = "Begin a quiz",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('GET_QUIZ')")
    @GetMapping("/begin/{quizId}")
    public QuizDTO beginQuiz(@PathVariable UUID quizId) {
        return quizService.beginQuiz(quizId);
    }

    @Operation(
            summary = "Check multiple choice answers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('CHECK_QUIZ')")
    @PostMapping("/checkMultipleChoice/{studentId}/{quizId}")
    public String checkMultipleChoice(@RequestBody List<Response> responseList, @PathVariable Long studentId, @PathVariable UUID quizId){
        return quizService.checkingMultipleChoiceQuestions(responseList, studentId, quizId);
    }

    @Operation(
            summary = "Get upcoming quiz task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('GET_QUIZ')")
    @GetMapping("/getUpcomingTask")
    public UpcomingTaskInfo getUpcomingTasks(){
        return quizService.getUpcomingQuizInfo();
    }
}
