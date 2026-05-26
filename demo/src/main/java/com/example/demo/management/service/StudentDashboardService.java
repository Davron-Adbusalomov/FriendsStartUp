package com.example.demo.management.service;

import com.example.demo.management.dto.*;
import com.example.demo.management.dto.projection.AttendanceSummaryProjection;
import com.example.demo.management.dto.projection.StudentGroupLessonProgressProjection;
import com.example.demo.management.dto.projection.StudentQuizPerformanceProjection;
import com.example.demo.management.dto.projection.StudentStatsProjection;
import com.example.demo.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StudentDashboardService {

    private final StudentRepository studentRepository;
    private final BadgeService badgeService;

    public StudentDashboardDTO getDashboard(Long studentId, String month, Locale locale) {
        StudentDashboardDTO dto = new StudentDashboardDTO();

        List<StudentQuizPerformanceDTO> quizPerformance = getQuizPerformance(studentId);
        dto.setStats(getStats(studentId, quizPerformance));
        dto.setAttendance(getAttendanceSummary(studentId, month));
        dto.setQuizPerformance(quizPerformance);
        dto.setLessonProgress(getLessonProgress(studentId));
        dto.setUpcomingTasks(getUpcomingTasks(studentId));
        dto.setBadges(badgeService.getStudentBadges(studentId, locale));

        return dto;
    }

    private StudentStatsDTO getStats(Long studentId, List<StudentQuizPerformanceDTO> quizPerformance) {
        StudentStatsProjection raw = studentRepository.getDashboardStats(studentId);

        double avgScore = quizPerformance.isEmpty() ? 0.0
                : Math.round(quizPerformance.stream()
                .mapToDouble(StudentQuizPerformanceDTO::getPercentage)
                .average()
                .orElse(0.0) * 10) / 10.0;

        StudentStatsDTO dto = new StudentStatsDTO();
        dto.setTotalGroups(raw.getTotalGroups() != null ? raw.getTotalGroups() : 0);
        dto.setCompletedLessons(raw.getCompletedLessons() != null ? raw.getCompletedLessons() : 0);
        dto.setTotalLessons(raw.getTotalLessons() != null ? raw.getTotalLessons() : 0);
        dto.setTotalBadges(raw.getTotalBadges() != null ? raw.getTotalBadges() : 0);
        dto.setAvgQuizScore(avgScore);
        return dto;
    }

    private AttendanceSummaryDTO getAttendanceSummary(Long studentId, String month) {
        YearMonth ym = (month != null && !month.isBlank())
                ? YearMonth.parse(month)
                : YearMonth.now();

        AttendanceSummaryProjection raw = studentRepository.getAttendanceSummary(
                studentId, ym.getMonthValue(), ym.getYear());

        AttendanceSummaryDTO dto = new AttendanceSummaryDTO();
        if (raw != null) {
            dto.setPresentPercentage(raw.getPresentPercentage() != null ? raw.getPresentPercentage() : 0);
            dto.setAbsentPercentage(raw.getAbsentPercentage() != null ? raw.getAbsentPercentage() : 0);
        }
        return dto;
    }

    private List<StudentQuizPerformanceDTO> getQuizPerformance(Long studentId) {
        List<StudentQuizPerformanceProjection> raw = studentRepository.getQuizPerformance(studentId);

        return raw.stream()
                .map(p -> new StudentQuizPerformanceDTO(
                        p.getQuizTitle(),
                        p.getGroupName(),
                        p.getScore() != null ? p.getScore() : 0L,
                        p.getTotalPoints() != null ? p.getTotalPoints() : 0L,
                        p.getPercentage() != null ? p.getPercentage() : 0.0,
                        p.getQuizDate()
                ))
                .toList();
    }

    private List<StudentGroupLessonProgressDTO> getLessonProgress(Long studentId) {
        List<StudentGroupLessonProgressProjection> raw = studentRepository.getLessonProgress(studentId);

        return raw.stream()
                .map(p -> {
                    int completed = p.getCompletedLessons() != null ? p.getCompletedLessons() : 0;
                    int total = p.getTotalLessons() != null ? p.getTotalLessons() : 0;
                    double percentage = total > 0 ? Math.round(completed * 100.0 / total * 10) / 10.0 : 0.0;
                    return new StudentGroupLessonProgressDTO(p.getGroupName(), completed, total, percentage);
                })
                .toList();
    }

    private List<StudentUpcomingTaskDTO> getUpcomingTasks(Long studentId) {
        return studentRepository.getUpcomingTasks(studentId)
                .stream()
                .map(p -> new StudentUpcomingTaskDTO(
                        p.getTitle(),
                        p.getType(),
                        p.getGroupName(),
                        p.getScheduledTime()
                ))
                .toList();
    }
}
