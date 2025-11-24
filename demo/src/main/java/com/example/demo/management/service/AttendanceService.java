package com.example.demo.management.service;

import com.example.demo.enums.AttendanceStatus;
import com.example.demo.management.dto.AttendanceDto;
import com.example.demo.management.dto.request.AttendanceCreateRequest;
import com.example.demo.management.mapper.AttendanceMapper;
import com.example.demo.management.model.Attendance;
import com.example.demo.management.repository.AttendanceRepository;
import com.example.demo.management.specification.AttendanceSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper mapper;

    public List<AttendanceDto> create(AttendanceCreateRequest request) {

        List<Attendance> attendances = request.getStudentIds().stream()
                .map(studentId -> {
                    Attendance attendance = new Attendance();
                    attendance.setStudentId(studentId);
                    attendance.setGroupId(request.getGroupId());
                    attendance.setAttendanceTime(request.getAttendanceTime());
                    attendance.setAttendanceStatus(request.getStatus());
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
            Pageable pageable
    ) {

        Specification<Attendance> spec = Specification
                .where(AttendanceSpecification.hasStudentName(fullName))
                .and(AttendanceSpecification.hasGroupName(groupName))
                .and(AttendanceSpecification.dateBetween(from, to));

        Page<Attendance> attendances = attendanceRepository.findAll(spec, pageable);
        return attendances.map(mapper::toDto);
    }


    public AttendanceDto getById(UUID id) {
        Attendance attendance = attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));
        return mapper.toDto(attendance);
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
}
