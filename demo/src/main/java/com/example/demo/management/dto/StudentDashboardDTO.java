package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardDTO {
    private StudentStatsDTO stats;
    private AttendanceSummaryDTO attendance;
    private List<StudentQuizPerformanceDTO> quizPerformance;
    private List<StudentGroupLessonProgressDTO> lessonProgress;
    private List<StudentUpcomingTaskDTO> upcomingTasks;
    private List<BadgeDTO> badges;
}
