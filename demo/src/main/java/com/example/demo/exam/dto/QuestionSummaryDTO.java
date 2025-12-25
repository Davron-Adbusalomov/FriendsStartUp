package com.example.demo.exam.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class QuestionSummaryDTO {
    private UUID id;

    private String title;

    private String level;

    private String type;

    private UUID subjectId;

    private String subjectName;

    private int mark;

    private Long teacherId;

    private String teacherName;

    private String topic;
}
