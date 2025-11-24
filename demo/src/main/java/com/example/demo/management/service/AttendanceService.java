package com.example.demo.management.service;

import com.example.demo.enums.AttendanceStatus;
import com.example.demo.management.dto.AttendanceDto;
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

    public AttendanceDto create(AttendanceDto attendanceDto) {
        Attendance attendance =  attendanceRepository.save(mapper.toEntity(attendanceDto));
        return mapper.toDto(attendance);
    }

    public Page<Attendance> getFiltered(
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

        return attendanceRepository.findAll(spec, pageable);
    }


    public AttendanceDto getById(UUID id) {
        Attendance attendance = attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));
        return mapper.toDto(attendance);
    }

    public AttendanceDto update(UUID id, Attendance updated) {
        AttendanceDto attendance = getById(id);

        attendance.setStudentId(updated.getStudentId());
        attendance.setGroupId(updated.getGroupId());
        attendance.setAttendanceTime(updated.getAttendanceTime());
        attendance.setAttendanceStatus(updated.getAttendanceStatus());

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
