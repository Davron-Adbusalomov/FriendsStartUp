package com.example.demo.exam.controller;

import com.example.demo.exam.dto.RecordedAnswerDTO;
import com.example.demo.exam.dto.StudentWrittenAnswersDTO;
import com.example.demo.exam.service.StudentAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController("/api/v1/student-answer")
@AllArgsConstructor
public class StudentAnswerController {

    private final StudentAnswerService studentAnswerService;

    @Operation(
            summary = "Get recorded student answers list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('BEGIN_QUIZ')")
    @GetMapping("")
    public List<RecordedAnswerDTO> getRecordedAnswers(@RequestParam(name = "quizId") UUID quizId,
                                                        @RequestParam(name = "studentId") Long studentId) {
        return studentAnswerService.getRecordedAnswers(quizId, studentId);
    }

    @Operation(
            summary = "Get written questions list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAnyAuthority('GET_WRITTEN_ANSWERS')")
    @GetMapping("/written/{quizId}")
    public List<StudentWrittenAnswersDTO> getWrittenAnswers(@PathVariable(name = "quizId") UUID quizId) {
        return studentAnswerService.getWrittenAnswers(quizId);
    }
}
