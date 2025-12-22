package com.example.demo.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private UUID id;

    private String title;

    private String level;

    private String type;

    private UUID subjectId;

    private String image;

    private String right_answer;

    private int mark;

    private Long teacherId;

    private List<String> options;
}
