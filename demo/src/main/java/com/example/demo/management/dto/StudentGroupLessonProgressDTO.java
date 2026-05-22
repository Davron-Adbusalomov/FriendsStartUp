package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGroupLessonProgressDTO {
    private String groupName;
    private int completedLessons;
    private int totalLessons;
    private double percentage;
}
