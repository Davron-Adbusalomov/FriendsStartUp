package com.example.demo.test.controller;

import com.example.demo.test.dto.QuestionDTO;
import com.example.demo.test.model.Question;
import com.example.demo.test.service.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/question")
public class QuestionController {
    @Autowired
    public QuestionService questionService;

    @GetMapping("getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        try {
            Question question = questionService.getQuestionById(id);
            return ResponseEntity.status(HttpStatus.OK).body(question);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("getQuestionsByGroupName/{groupName}")
    public ResponseEntity<?> getByGroup(@PathVariable String groupName){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(questionService.getQuestionByGroupName(groupName));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("getAll")
    public ResponseEntity<?> getAll(){
        return questionService.getAllQuestions();
    }

    @PostMapping("create")
    public ResponseEntity<?> creatQuestion(@RequestBody QuestionDTO questionDTO){
        return questionService.createQuestion(questionDTO);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateQuestion(@RequestBody QuestionDTO questionDTO, @PathVariable Long id){
        return questionService.updateQuestion(questionDTO, id);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id){
        return questionService.deleteQuestion(id);
    }
}
