package com.example.demo.management.dto.projection;

public interface StudentStatsProjection {
    Integer getTotalGroups();
    Integer getCompletedLessons();
    Integer getTotalLessons();
    Integer getTotalBadges();
}
