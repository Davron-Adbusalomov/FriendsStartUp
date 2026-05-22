package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyActivityDTO {
    private String month;
    private double avgQuizScore;
    private double lessonCompletionRate;
}
