package com.example.demo.exam.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WrittenAnswerDTO {
    private UUID questionId;

    private String studentAnswer;

    private String correctAnswer;

    private Integer score;

    private String questionTitle;

}
