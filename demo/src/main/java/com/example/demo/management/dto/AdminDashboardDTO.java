package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private AdminStatsDTO stats;
    private AttendanceSummaryDTO attendance;
    private List<DailyAttendanceDTO> weeklyAttendance;
    private List<MonthlyActivityDTO> monthlyActivity;
    private List<AgendaDTO> agenda;
    private List<TopPerformerDTO> topPerformers;
}
