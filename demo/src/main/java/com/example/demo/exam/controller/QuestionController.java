package com.example.demo.exam.controller;

import com.example.demo.exam.dto.QuestionDTO;
import com.example.demo.exam.dto.QuestionRequestDTO;
import com.example.demo.exam.dto.QuestionSummaryDTO;
import com.example.demo.exam.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public Page<QuestionSummaryDTO> getAll(@RequestParam(name = "level", required = false) String level,
                                           @RequestParam(name = "teacherId", required = false) Long teacherId,
                                           @RequestParam(name = "quizId", required = false) UUID quizId,
                                           @RequestParam(name = "search", required = false) String search,
                                           @RequestParam(name = "subjectId", required = false) UUID subjectId,
                                           Pageable pageable) {
        return questionService.getAllQuestions(level, teacherId, quizId, search, subjectId, pageable);
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
    @PostMapping(value ="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> creatQuestion(@ModelAttribute QuestionRequestDTO questionDTO) {
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
    @PutMapping(value ="update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateQuestion(@ModelAttribute QuestionRequestDTO questionDTO, @PathVariable UUID id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(questionService.updateQuestion(questionDTO, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
