package com.example.demo.management.dto.projection;

public interface DailyAttendanceProjection {
    String getDayOfWeek();
    Integer getPresentCount();
    Integer getAbsentCount();
}
