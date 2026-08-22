package com.example.demo.management.service;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.management.dto.*;
import com.example.demo.management.dto.projection.*;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.payment.service.FinanceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private final TeacherRepository teacherRepository;
    private final FinanceReportService financeReportService;

    public TeacherDashboardDTO getDashboard(Long teacherId, String groupId, String month) {

        TeacherDashboardDTO dto = new TeacherDashboardDTO();

        dto.setStats(getStats(teacherId));
        dto.setPerformance(getPerformance(CurrentUserUtils.getUserId()));
        dto.setActivity(getTeachingActivity(teacherId));
        dto.setStudentTasks(getStudentTasks());
        dto.setAgenda(getAgenda(teacherId));
        dto.setAttendance(getGroupAttendanceSummary(teacherId, groupId, month));

        return dto;
    }

    public AttendanceSummaryDTO getGroupAttendanceSummary(Long teacherId, String groupId, String month) {
        AttendanceSummaryDTO dto = new AttendanceSummaryDTO();
        if (groupId == null || groupId.isBlank()) {
            return dto;
        }

        YearMonth ym = (month != null && !month.isBlank())
                ? YearMonth.parse(month)
                : YearMonth.now();

        AttendanceSummaryProjection attendance = teacherRepository.findAttendanceByGroupAndMonth(
                teacherId, groupId, ym.getMonthValue(), ym.getYear());

        if (attendance != null) {
            dto.setPresentPercentage(attendance.getPresentPercentage() != null ? attendance.getPresentPercentage() : 0);
            dto.setAbsentPercentage(attendance.getAbsentPercentage() != null ? attendance.getAbsentPercentage() : 0);
            dto.setLatePercentage(attendance.getLatePercentage() != null ? attendance.getLatePercentage() : 0);
        }
        return dto;
    }

    private StatsDTO getStats(Long teacherId) {

        TeacherStatsProjection stats =
                teacherRepository.getStats(teacherId);

        StatsDTO dto = new StatsDTO();

        dto.setTotalClasses(
                stats.getTotalClasses() != null
                        ? stats.getTotalClasses().intValue()
                        : 0
        );

        dto.setTotalStudents(
                stats.getTotalStudents() != null
                        ? stats.getTotalStudents().intValue()
                        : 0
        );

        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        dto.setTotalSalary(teacher != null && teacher.getMonthlySalary() != null
                ? teacher.getMonthlySalary().doubleValue() : 0);
        BigDecimal monthlyIncome = financeReportService.monthlyIncomeForTeacher(teacherId, YearMonth.now());
        dto.setTotalIncome(monthlyIncome != null ? monthlyIncome.doubleValue() : 0);

        dto.setClassesIncrease(0);
        dto.setStudentsIncrease(0);
        dto.setSalaryIncrease(0);
        dto.setIncomeIncrease(0);

        return dto;
    }

    private List<PerformanceDTO> getPerformance(Long teacherId) {
        List<PerformanceProjection> rawData = teacherRepository.getQuizPerformanceData(teacherId);

        return rawData.stream()
                .collect(Collectors.groupingBy(
                        PerformanceProjection::getQuizTitle,
                        LinkedHashMap::new,
                        Collectors.toMap(
                                PerformanceProjection::getGroupName,
                                p -> p.getPercentage() != null ? p.getPercentage() : 0.0,
                                (a, b) -> (a + b) / 2.0
                        )
                ))
                .entrySet().stream()
                .map(entry -> new PerformanceDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<TeachingActivityDTO> getTeachingActivity(Long teacherId) {

        return teacherRepository.getTeachingActivity(teacherId)
                .stream()
                .map(a -> new TeachingActivityDTO(
                        a.getMonth(),
                        a.getClasses()
                ))
                .toList();
    }

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

    private List<AgendaDTO> getAgenda(Long teacherId) {

        return teacherRepository.getAgenda(teacherId)
                .stream()
                .map(a -> new AgendaDTO(
                        a.getTitle(),
                        a.getTime(),
                        a.getRoom()
                ))
                .toList();
    }
}