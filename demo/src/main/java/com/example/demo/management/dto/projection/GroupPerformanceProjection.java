package com.example.demo.management.dto.projection;

public interface GroupPerformanceProjection {
    String getGroupName();
    Integer getStudentCount();
    Double getAvgQuizScore();
    Double getLessonCompletion();
    Double getAttendanceRate();
}
