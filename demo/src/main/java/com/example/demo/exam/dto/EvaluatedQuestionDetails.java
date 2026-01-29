package com.example.demo.exam.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class EvaluatedQuestionDetails {
    private UUID id;

    private String title;

    private String level;

    private String type;

    private UUID subjectId;

    private String subjectName;

    private String image;

    private String rightAnswer;

    private Integer mark;

    private Long teacherId;

    private String teacherName;

    private String topic;

    private List<String> options;

    private String studentAnswer;

    private Boolean isCorrect;

    private Integer score;
}
