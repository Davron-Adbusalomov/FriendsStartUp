package com.example.demo.management.dto.projection;

public interface MonthlyActivityProjection {
    String getMonth();
    Double getAvgQuizScore();
    Double getLessonCompletionRate();
}
