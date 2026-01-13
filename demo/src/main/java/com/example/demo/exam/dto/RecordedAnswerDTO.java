package com.example.demo.exam.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RecordedAnswerDTO {
    private UUID quizId;

    private UUID questionId;

    private String answer;
}
