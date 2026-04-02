package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.AttendanceStatus;
import com.example.demo.management.dto.AttendanceDto;
import com.example.demo.management.dto.AttendanceMatrixDto;
import com.example.demo.management.dto.request.AttendanceCreateRequest;
import com.example.demo.management.mapper.AttendanceMapper;
import com.example.demo.management.model.Attendance;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.AttendanceRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.specification.AttendanceSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper mapper;
    private final StudentRepository studentRepository;

    public List<AttendanceDto> create(AttendanceCreateRequest request) {

        List<Attendance> attendances = request.getStudentIds().stream()
                .map(studentId -> {
                    Attendance attendance = new Attendance();
                    attendance.setStudentId(studentId);
                    attendance.setGroupId(request.getGroupId());
                    attendance.setAttendanceTime(request.getAttendanceTime());
                    attendance.setAttendanceStatus(request.getStatus());
                    attendance.setCenterId(TenantContext.getCenterId());
                    return attendance;
                })
                .toList();

        List<Attendance> saved = attendanceRepository.saveAll(attendances);

        return saved.stream().map(mapper::toDto).toList();
    }

    public Page<AttendanceDto> getAll(
            String fullName,
            String groupName,
            LocalDateTime from,
            LocalDateTime to,
            Long studentId,
            Pageable pageable
    ) {

        Specification<Attendance> spec = Specification
                .where(AttendanceSpecification.hasStudentName(fullName))
                .and(AttendanceSpecification.hasGroupName(groupName))
                .and(AttendanceSpecification.dateBetween(from, to))
                .and(AttendanceSpecification.hasStudentId(studentId));

        Page<Attendance> attendances = attendanceRepository.findAll(spec, pageable);
        return attendances.map(mapper::toDto);
    }



    public AttendanceDto getById(UUID id) {
        Attendance attendance = attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));
        return mapper.toDto(attendance);
    }

    public AttendanceStatus getTodayAttendanceStatus(Long studentId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<Attendance> attendances = attendanceRepository.findAllByStudentIdAndAttendanceTimeBetweenOrderByAttendanceTimeDesc(studentId, startOfDay, endOfDay);
        if (attendances.isEmpty()) {
            return AttendanceStatus.NOT_MARKED;
        }

        return attendances.get(0).getAttendanceStatus();
    }

    public AttendanceDto update(UUID id, AttendanceDto updated) {
        AttendanceDto attendance = getById(id);

        if (updated.getStudentId() != null) attendance.setStudentId(updated.getStudentId());
        if (updated.getGroupId() != null) attendance.setGroupId(updated.getGroupId());
        if (updated.getAttendanceTime() != null) attendance.setAttendanceTime(updated.getAttendanceTime());
        if (updated.getAttendanceStatus() != null) attendance.setAttendanceStatus(updated.getAttendanceStatus());

        return mapper.toDto(attendanceRepository.save(mapper.toEntity(attendance)));
    }

    public void delete(UUID id) {
        Attendance attendance = attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));
        attendanceRepository.delete(attendance);
    }

    public Attendance restore(UUID id) {
        AttendanceDto attendance = getById(id);
        attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
        return attendanceRepository.save(mapper.toEntity(attendance));
    }

    public List<AttendanceMatrixDto> getAllOptimized(
            UUID groupId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {

        // 1. GET ALL STUDENTS IN GROUP
        List<Student> students = studentRepository.findAllByGroupId(groupId);

        // 2. GET ALL ATTENDANCE IN RANGE
        Specification<Attendance> spec = Specification
                .where(AttendanceSpecification.hasGroupId(groupId))
                .and(AttendanceSpecification.dateBetween(from, to));

        Page<Attendance> attendances = attendanceRepository.findAll(spec, pageable);

        // 3. MAP attendance -> studentId -> date -> status
        Map<Long, Map<String, AttendanceStatus>> attendanceMap = new HashMap<>();

        for (Attendance att : attendances) {
            Long studentId = att.getStudentId();

            String date = att.getAttendanceTime()
                    .toString(); // yyyy-MM-dd

            attendanceMap
                    .computeIfAbsent(studentId, k -> new HashMap<>())
                    .put(date, att.getAttendanceStatus());
        }

        // 4. BUILD FINAL RESULT (ALL STUDENTS)
        List<AttendanceMatrixDto> result = new ArrayList<>();

        for (Student student : students) {
            AttendanceMatrixDto dto = new AttendanceMatrixDto();

            dto.setStudentId(student.getId());
            dto.setStudentName(student.getFullName());

            dto.setAttendance(
                    attendanceMap.getOrDefault(student.getId(), new HashMap<>())
            );

            result.add(dto);
        }

        return result;
    }
}
