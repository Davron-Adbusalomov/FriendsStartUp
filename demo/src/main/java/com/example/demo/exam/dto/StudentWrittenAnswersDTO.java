package com.example.demo.exam.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentWrittenAnswersDTO {
    private Long studentId;
    private String studentName;
    private List<WrittenAnswerDTO> answers;
}

