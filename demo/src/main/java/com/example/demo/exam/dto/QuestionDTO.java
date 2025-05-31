package com.example.demo.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;

    private String title;

    private String level;

    private String type;

    private String subject;

    private String image;

    private String right_answer;

    private int mark;

    private Long teacherId;

    private LocalDateTime createdAt;

    private List<String> options;
}
