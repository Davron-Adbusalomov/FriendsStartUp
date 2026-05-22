package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizPerformanceDTO {
    private String quizTitle;
    private String groupName;
    private long score;
    private long totalPoints;
    private double percentage;
    private LocalDateTime quizDate;
}
