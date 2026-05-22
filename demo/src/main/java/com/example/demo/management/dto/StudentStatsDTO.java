package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatsDTO {
    private int totalGroups;
    private int completedLessons;
    private int totalLessons;
    private double avgQuizScore;
    private int totalBadges;
}
