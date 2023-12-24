package com.example.demo.test.dto;

import com.example.demo.management.model.Student;
import com.example.demo.test.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quiz_ResultsDTO {
    private Long id;

    private Long mark;

    private Quiz quiz;

    private Student student;
}
