package com.example.demo.exam.controller;

import com.example.demo.exam.dto.QuestionDTO;
import com.example.demo.exam.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("api/v1/question")
public class QuestionController {
    @Autowired
    public QuestionService questionService;

    @Operation(
            summary = "Get question by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('GET_QUESTION')")
    @GetMapping("/{id}")
    public QuestionDTO getById(@PathVariable UUID id) {
        return questionService.getQuestionById(id);
    }

    @Operation(
            summary = "Getting group by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('GET_QUESTION_BY_LEVEL')")
    @GetMapping("/getQuestionsByLevel/{level}")
    public ResponseEntity<?> getByGroup(@PathVariable String level) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(questionService.getQuestionByLevel(level));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Getting all questions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @GetMapping
    @PreAuthorize("hasAuthority('GET_QUESTIONS_LIST')")
    public Page<QuestionDTO> getAll(@RequestParam(required = false) String level,
                                    @RequestParam(required = false) Long teacherId,
                                    @RequestParam(required = false) UUID quizId,
                                    Pageable pageable) {
        return questionService.getAllQuestions(level, teacherId, quizId, pageable);
    }

    @Operation(
            summary = "Create a new question",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('CREATE_QUESTION')")
    @PostMapping("/create")
    public ResponseEntity<?> creatQuestion(@RequestBody QuestionDTO questionDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(questionService.createQuestion(questionDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "update question by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('UPDATE_QUESTION')")
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateQuestion(@RequestBody QuestionDTO questionDTO, @PathVariable UUID id) {
        return questionService.updateQuestion(questionDTO, id);
    }

    @Operation(
            summary = "Delete question by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('DELETE_QUESTION')")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable UUID id) {
        return questionService.deleteQuestion(id);
    }
}
