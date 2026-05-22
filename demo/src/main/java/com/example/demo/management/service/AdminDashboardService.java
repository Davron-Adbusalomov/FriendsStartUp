package com.example.demo.management.service;

import com.example.demo.management.dto.*;
import com.example.demo.management.dto.projection.*;
import com.example.demo.management.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AdminRepository adminRepository;

    public AdminDashboardDTO getDashboard(UUID centerId, String month) {
        YearMonth ym = (month != null && !month.isBlank())
                ? YearMonth.parse(month)
                : YearMonth.now();

        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setStats(getStats(centerId));
        dto.setAttendance(getAttendanceSummary(centerId, ym));
        dto.setWeeklyAttendance(getWeeklyAttendance(centerId, ym));
        dto.setMonthlyActivity(getMonthlyActivity(centerId));
        dto.setAgenda(getAgenda(centerId));
        dto.setTopPerformers(getTopPerformers(centerId));
        dto.setGroupPerformance(getGroupPerformance(centerId));
        return dto;
    }

    private AdminStatsDTO getStats(UUID centerId) {
        AdminStatsProjection raw = adminRepository.getStats(centerId);
        return new AdminStatsDTO(
                raw.getTotalStudents() != null ? raw.getTotalStudents() : 0L,
                raw.getTotalTeachers() != null ? raw.getTotalTeachers() : 0L,
                raw.getTotalGroups()   != null ? raw.getTotalGroups()   : 0L,
                raw.getTotalBadges()   != null ? raw.getTotalBadges()   : 0L
        );
    }

    private AttendanceSummaryDTO getAttendanceSummary(UUID centerId, YearMonth ym) {
        AttendanceSummaryProjection raw = adminRepository.getAttendanceSummary(
                centerId, ym.getMonthValue(), ym.getYear());
        AttendanceSummaryDTO dto = new AttendanceSummaryDTO();
        if (raw != null) {
            dto.setPresentPercentage(raw.getPresentPercentage() != null ? raw.getPresentPercentage() : 0);
            dto.setAbsentPercentage(raw.getAbsentPercentage()   != null ? raw.getAbsentPercentage()  : 0);
        }
        return dto;
    }

    private List<DailyAttendanceDTO> getWeeklyAttendance(UUID centerId, YearMonth ym) {
        return adminRepository.getWeeklyAttendance(centerId, ym.getMonthValue(), ym.getYear())
                .stream()
                .map(p -> new DailyAttendanceDTO(
                        p.getDayOfWeek(),
                        p.getPresentCount() != null ? p.getPresentCount() : 0,
                        p.getAbsentCount()  != null ? p.getAbsentCount()  : 0
                ))
                .toList();
    }

    private List<MonthlyActivityDTO> getMonthlyActivity(UUID centerId) {
        return adminRepository.getMonthlyActivity(centerId)
                .stream()
                .map(p -> new MonthlyActivityDTO(
                        p.getMonth(),
                        p.getAvgQuizScore()        != null ? p.getAvgQuizScore()        : 0.0,
                        p.getLessonCompletionRate() != null ? p.getLessonCompletionRate() : 0.0
                ))
                .toList();
    }

    private List<AgendaDTO> getAgenda(UUID centerId) {
        return adminRepository.getAgenda(centerId)
                .stream()
                .map(p -> new AgendaDTO(p.getTitle(), p.getTime(), p.getRoom()))
                .toList();
    }

    private List<TopPerformerDTO> getTopPerformers(UUID centerId) {
        return adminRepository.getTopPerformers(centerId)
                .stream()
                .map(p -> new TopPerformerDTO(
                        p.getStudentName(),
                        p.getGroupName(),
                        p.getScore() != null ? p.getScore() : 0.0,
                        p.getRank()  != null ? p.getRank()  : 0L
                ))
                .toList();
    }

    private List<GroupPerformanceDTO> getGroupPerformance(UUID centerId) {
        return adminRepository.getGroupPerformance(centerId)
                .stream()
                .map(p -> new GroupPerformanceDTO(
                        p.getGroupName(),
                        p.getStudentCount()   != null ? p.getStudentCount()   : 0,
                        p.getAvgQuizScore()   != null ? p.getAvgQuizScore()   : 0.0,
                        p.getLessonCompletion() != null ? p.getLessonCompletion() : 0.0,
                        p.getAttendanceRate() != null ? p.getAttendanceRate() : 0.0
                ))
                .toList();
    }
}
