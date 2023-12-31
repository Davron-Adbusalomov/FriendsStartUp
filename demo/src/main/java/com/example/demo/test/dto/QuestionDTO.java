package com.example.demo.test.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;

    private String text;

    private Double mark;

    private List<OptionDTO> options = new ArrayList<>();

    private Long quizId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
