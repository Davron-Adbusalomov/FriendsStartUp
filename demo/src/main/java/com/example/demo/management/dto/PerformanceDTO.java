package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDTO {
    private String label; // This will be the Quiz Title
    private Map<String, Double> groupScores; // Key: Group Name, Value: Percentage
}
