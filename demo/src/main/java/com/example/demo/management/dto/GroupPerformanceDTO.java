package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPerformanceDTO {
    private String groupName;
    private int studentCount;
    private double avgQuizScore;
    private double lessonCompletion;
    private double attendanceRate;
}
