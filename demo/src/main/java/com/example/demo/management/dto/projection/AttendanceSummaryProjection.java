package com.example.demo.management.dto.projection;

public interface AttendanceSummaryProjection {
    Integer getPresentPercentage();
    Integer getAbsentPercentage();
    Integer getLatePercentage();
}
