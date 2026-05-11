package com.example.demo.management.dto.projection;

public interface TeacherStatsProjection {

    Long getTotalClasses();

    Long getTotalStudents();

    Long getTotalPresent();

    Long getTotalAbsent();
}
