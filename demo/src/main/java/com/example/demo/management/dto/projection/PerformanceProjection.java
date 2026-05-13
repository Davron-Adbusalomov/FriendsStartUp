package com.example.demo.management.dto.projection;

import java.time.LocalDateTime;

public interface PerformanceProjection {
    String getQuizTitle();
    String getGroupName();
    Double getPercentage();
    LocalDateTime getQuizDate();
}