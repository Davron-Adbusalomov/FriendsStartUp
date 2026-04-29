package com.example.demo.management.service;

import com.example.demo.management.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    public TeacherDashboardDTO getDashboard() {
        TeacherDashboardDTO dto = new TeacherDashboardDTO();

        dto.setStats(getStats());
//        dto.setAttendance(getAttendance());
        dto.setPerformance(getPerformance());
        dto.setActivity(getTeachingActivity());
//        dto.setTasks(getTasks());
        dto.setStudentTasks(getStudentTasks());
        dto.setAgenda(getAgenda());

        return dto;
    }

    private StatsDTO getStats() {
        StatsDTO stats = new StatsDTO();
        stats.setTotalClasses(147);
        stats.setClassesIncrease(9.18);
        stats.setTotalStudents(3250);
        stats.setStudentsIncrease(4.5);
        stats.setTotalSalary(104687);
        stats.setSalaryIncrease(6.1);
        stats.setTotalIncome(1682500);
        stats.setIncomeIncrease(4.38);
        return stats;
    }

//    private List<AttendanceDTO> getAttendance() {
//        return List.of(
//                new AttendanceDTO("Present", 80, "#A387F7"),
//                new AttendanceDTO("Absent", 20, "#F3F0FE")
//        );
//    }

    private List<PerformanceDTO> getPerformance() {
        return List.of(
                new PerformanceDTO("Mon", 75, 85, 65),
                new PerformanceDTO("Tue", 60, 70, 80),
                new PerformanceDTO("Wed", 80, 75, 70),
                new PerformanceDTO("Thu", 70, 90, 75),
                new PerformanceDTO("Fri", 85, 65, 85)
        );
    }

    private List<TeachingActivityDTO> getTeachingActivity() {
        return List.of(
                new TeachingActivityDTO("Jan", 30),
                new TeachingActivityDTO("Feb", 50),
                new TeachingActivityDTO("Mar", 45)
                // continue...
        );
    }

//    private List<TaskDTO> getTasks() {
//        return List.of(
//                new TaskDTO(1L, "Grade Student Essays", "2024-04-25", false),
//                new TaskDTO(2L, "Update Lesson Plan", "2024-04-28", false)
//        );
//    }

    private List<StudentTaskDTO> getStudentTasks() {
        return List.of(
                new StudentTaskDTO(
                        "Emily Peterson",
                        "History 11A",
                        "World War I",
                        "Essay on WWI",
                        "2024-04-30",
                        "active"
                )
        );
    }

    private List<AgendaDTO> getAgenda() {
        return List.of(
                new AgendaDTO("History Class", "08:00 - 09:00", "Room 204"),
                new AgendaDTO("Lecture", "10:00 - 11:30", "Room 101")
        );
    }
}
