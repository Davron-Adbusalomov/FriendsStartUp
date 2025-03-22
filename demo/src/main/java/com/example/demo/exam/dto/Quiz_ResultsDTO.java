package com.example.demo.exam.dto;

import com.example.demo.management.model.Student;
import com.example.demo.exam.model.Quiz;
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
