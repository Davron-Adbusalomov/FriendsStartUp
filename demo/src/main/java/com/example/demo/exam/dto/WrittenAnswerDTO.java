package com.example.demo.exam.dto;

import lombok.Data;

@Data
public class WrittenAnswerDTO {

    private String studentAnswer;

    private String correctAnswer;

    private Long score;

    private String questionTitle;

}
