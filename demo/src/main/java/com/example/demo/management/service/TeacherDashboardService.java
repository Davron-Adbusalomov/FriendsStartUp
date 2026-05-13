package com.example.demo.management.service;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.management.dto.*;
import com.example.demo.management.dto.projection.*;
import com.example.demo.management.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private final TeacherRepository teacherRepository;

    @Cacheable(value = "teacher-dashboard", key = "#teacherId")
    public TeacherDashboardDTO getDashboard(Long teacherId) {

        TeacherDashboardDTO dto = new TeacherDashboardDTO();

        dto.setStats(getStats(teacherId));
        dto.setPerformance(getPerformance(CurrentUserUtils.getUserId()));
        dto.setActivity(getTeachingActivity(teacherId));
        dto.setStudentTasks(getStudentTasks());
        dto.setAgenda(getAgenda(teacherId));

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

        dto.setTotalSalary(0);
        dto.setTotalIncome(0);

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
                                p -> p.getPercentage() != null ? p.getPercentage() : 0.0
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