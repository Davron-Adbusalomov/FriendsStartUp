package com.example.demo.exam.dto;

import com.example.demo.enums.QuizContentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QuizSummaryDTO {
    private UUID id;
    private String title;
    private Long duration;
    private int questionsNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
    private QuizContentStatus status;
}
