package com.example.demo.test.dto;

import com.example.demo.management.model.Student;
import com.example.demo.test.model.Option;
import com.example.demo.test.model.Question;
import com.example.demo.test.model.Quiz;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;

    private Question question;

    private Option selectedOption;

    private Quiz quiz;

    private Double questionMark; // Mark assigned to the question

    private boolean isCorrect; // Indicate whether the response is correct

    private Student student;

    private LocalDateTime submittedAt;
}
