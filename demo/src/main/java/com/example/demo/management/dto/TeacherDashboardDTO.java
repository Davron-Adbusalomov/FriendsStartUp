package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardDTO {
    private StatsDTO stats;
    private AttendanceDto attendance;
    private List<PerformanceDTO> performance;
    private List<AgendaDTO> agenda;
    private List<TeachingActivityDTO> activity;
    private List<StudentTaskDTO> studentTasks;
//    private List<TodoTaskDTO> tasks;
}
