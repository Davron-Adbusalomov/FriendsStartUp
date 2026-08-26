package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.AttendanceStatus;
import com.example.demo.enums.StudentType;
import com.example.demo.management.dto.AttendanceCellDto;
import com.example.demo.management.dto.AttendanceDto;
import com.example.demo.management.dto.AttendanceMatrixDto;
import com.example.demo.management.dto.request.AttendanceCreateRequest;
import com.example.demo.management.mapper.AttendanceMapper;
import com.example.demo.management.model.Attendance;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.AttendanceRepository;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.specification.AttendanceSpecification;
import com.example.demo.payment.dto.StudentBalanceResponse;
import com.example.demo.payment.service.InvoiceGenerationService;
import com.example.demo.payment.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper mapper;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final InvoiceGenerationService invoiceGenerationService;
    private final PaymentService paymentService;

    @Transactional
    public List<AttendanceDto> create(AttendanceCreateRequest request) {
        if (request.getStudentStatuses() == null) {
            return Collections.emptyList();
        }

        LocalDateTime day = request.getAttendanceTime().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = day.plusDays(1).minusNanos(1);

        // Load all existing records for this group + day in one query
        List<Attendance> existing = attendanceRepository.findByGroupIdAndDay(
                request.getGroupId(), day, endOfDay);
        Map<Long, Attendance> existingByStudent = new HashMap<>();
        for (Attendance a : existing) {
            existingByStudent.put(a.getStudentId(), a);
        }

        List<Attendance> toSave = request.getStudentStatuses().stream()
                .map(item -> {
                    Attendance attendance = existingByStudent.getOrDefault(
                            item.getStudentId(), new Attendance());
                    attendance.setStudentId(item.getStudentId());
                    attendance.setGroupId(request.getGroupId());
                    attendance.setAttendanceTime(day);
                    attendance.setAttendanceStatus(item.getStatus());
                    attendance.setCenterId(TenantContext.getCenterId());
                    return attendance;
                })
                .toList();

        List<Attendance> saved = attendanceRepository.saveAll(toSave);

        return saved.stream()
                .map(mapper::toDto)
                .toList();
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

        Pageable effectivePageable = pageable != null
                ? PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().and(Sort.by("attendanceTime").descending())
        )
                : PageRequest.of(0, 10, Sort.by("attendanceTime").descending());

        Page<Attendance> attendances = attendanceRepository.findAll(spec, effectivePageable);
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
        if (groupId == null) {
            throw new IllegalArgumentException("groupId is required");
        }

        Grouping group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        // 1. GET ALL STUDENTS IN GROUP, PAGINATED
        // (page/size describe a page of STUDENTS, not attendance rows — the attendance
        // range below is fetched in full for whichever students land on the page)
        List<Student> allStudents = studentRepository.findAllByGroupId(groupId);

        Pageable effectivePageable = pageable != null ? pageable : PageRequest.of(0, 100);
        int fromIndex = Math.min((int) effectivePageable.getOffset(), allStudents.size());
        int toIndex = Math.min(fromIndex + effectivePageable.getPageSize(), allStudents.size());
        List<Student> students = allStudents.subList(fromIndex, toIndex);

        // 2. GET ALL ATTENDANCE IN RANGE FOR THE GROUP
        Specification<Attendance> spec = Specification
                .where(AttendanceSpecification.hasGroupId(groupId))
                .and(AttendanceSpecification.dateBetween(from, to));

        List<Attendance> attendances = attendanceRepository.findAll(spec);

        // 3. MAP attendance -> studentId -> date -> status
        Map<Long, Map<String, AttendanceCellDto>> attendanceMap = new HashMap<>();

        for (Attendance att : attendances) {
            Long studentId = att.getStudentId();

            String date = att.getAttendanceTime()
                    .toLocalDate()
                    .toString(); // yyyy-MM-dd

            attendanceMap
                    .computeIfAbsent(studentId, k -> new HashMap<>())
                    .put(date, new AttendanceCellDto(att.getId(), att.getAttendanceStatus()));
        }

        // 4. BUILD FINAL RESULT (STUDENTS ON THE CURRENT PAGE)
        LocalDate currentPeriod = YearMonth.now().atDay(1);
        List<AttendanceMatrixDto> result = new ArrayList<>();

        for (Student student : students) {
            AttendanceMatrixDto dto = new AttendanceMatrixDto();

            dto.setStudentId(student.getId());
            dto.setStudentName(student.getFullName());
            dto.setPhone(student.getPhoneNumber());
            dto.setParentName(student.getParentName());
            dto.setParentPhone(student.getParentContact());

            boolean onTrial = invoiceGenerationService.isStillOnTrial(student.getId(), group, currentPeriod);
            dto.setStudentType(onTrial ? StudentType.TRIAL : StudentType.REGULAR);

            StudentBalanceResponse balance = paymentService.getStudentBalance(student.getId());
            dto.setBalance(balance.getTotalCredit());
            dto.setDebt(balance.getTotalOwed());

            dto.setAttendance(
                    attendanceMap.getOrDefault(student.getId(), new HashMap<>())
            );

            result.add(dto);
        }

        return result;
    }
}
