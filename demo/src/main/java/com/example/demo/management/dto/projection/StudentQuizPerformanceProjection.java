package com.example.demo.management.dto.projection;

import java.time.LocalDateTime;

public interface StudentQuizPerformanceProjection {
    String getQuizTitle();
    String getGroupName();
    Long getScore();
    Long getTotalPoints();
    Double getPercentage();
    LocalDateTime getQuizDate();
}
