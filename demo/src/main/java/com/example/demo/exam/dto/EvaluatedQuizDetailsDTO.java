package com.example.demo.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluatedQuizDetailsDTO {

    private UUID id;

    private String title;

    private Long duration;

    private int questionsNum;

    private UUID courseId;

    private Long teacherId;

    private LocalDateTime createdAt;

    private LocalDateTime startTime;

    private Set<EvaluatedQuestionDetails> questions;

    private Integer maxMark;

    private Integer obtainedMark;
}

