package com.example.demo.management.dto.projection;

public interface StudentGroupLessonProgressProjection {
    String getGroupName();
    Integer getCompletedLessons();
    Integer getTotalLessons();
}
