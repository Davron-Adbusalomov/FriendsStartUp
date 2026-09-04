package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.AttendanceStatus;
import com.example.demo.enums.StudentType;
import com.example.demo.management.dto.AttendanceCellDto;
import com.example.demo.management.dto.AttendanceDto;
import com.example.demo.management.dto.AttendanceMatrixDto;
import com.example.demo.management.dto.projection.StudentGroupIdProjection;
import com.example.demo.management.dto.request.AttendanceCreateRequest;
import com.example.demo.management.mapper.AttendanceMapper;
import com.example.demo.management.model.Attendance;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.AttendanceRepository;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.specification.AttendanceSpecification;
import com.example.demo.management.specification.GroupSpecification;
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
import java.util.stream.Collectors;

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
            Long teacherId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        // groupId is optional — when it's null, every group in the current center is in
        // scope (further narrowed to a teacher's own groups when teacherId is given). When a
        // specific groupId IS given, confirm it actually resolves (exists, right center/teacher)
        // with a cheap EXISTS check so an invalid id still 404s instead of silently coming back empty.
        if (groupId != null) {
            Specification<Grouping> existsSpec = Specification
                    .where(GroupSpecification.hasCenterId(TenantContext.getCenterId()))
                    .and(GroupSpecification.teacherIdEquals(teacherId))
                    .and(GroupSpecification.idEquals(groupId));
            if (!groupRepository.exists(existsSpec)) {
                throw new EntityNotFoundException("Group not found with id: " + groupId);
            }
        }

        // 1. PAGE OVER (student, group) PAIRS AT THE DATABASE LEVEL.
        // A student enrolled in several groups gets one row per group so per-group stats
        // don't collide. Paging here — instead of loading every matched student into memory —
        // is what keeps this cheap once groupId/teacherId are omitted and the scope is a whole center.
        Pageable effectivePageable = pageable != null
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())
                : PageRequest.of(0, 100);

        Page<StudentGroupIdProjection> page = groupRepository.findStudentGroupPairs(
                TenantContext.getCenterId(), teacherId, groupId, effectivePageable);

        List<StudentGroupIdProjection> pairs = page.getContent();
        if (pairs.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. BATCH-FETCH ONLY THE STUDENTS/GROUPS THAT ACTUALLY LAND ON THIS PAGE
        List<Long> studentIds = pairs.stream().map(StudentGroupIdProjection::getStudentId).distinct().toList();
        List<UUID> groupIds = pairs.stream().map(StudentGroupIdProjection::getGroupId).distinct().toList();

        Map<Long, Student> studentsById = studentRepository.findAllById(studentIds).stream()
                .collect(Collectors.toMap(Student::getId, s -> s));
        Map<UUID, Grouping> groupsById = groupRepository.findAllById(groupIds).stream()
                .collect(Collectors.toMap(Grouping::getId, g -> g));

        // 3. GET ATTENDANCE IN RANGE, SCOPED TO ONLY THOSE STUDENTS/GROUPS — not the whole center
        Specification<Attendance> spec = Specification
                .where(AttendanceSpecification.hasGroupIdIn(groupIds))
                .and(AttendanceSpecification.hasStudentIdIn(studentIds))
                .and(AttendanceSpecification.dateBetween(from, to));

        List<Attendance> attendances = attendanceRepository.findAll(spec);

        // 4. MAP attendance -> (studentId, groupId) -> date -> status
        Map<String, Map<String, AttendanceCellDto>> attendanceMap = new HashMap<>();

        for (Attendance att : attendances) {
            String key = attendanceKey(att.getStudentId(), att.getGroupId());

            String date = att.getAttendanceTime()
                    .toLocalDate()
                    .toString(); // yyyy-MM-dd

            attendanceMap
                    .computeIfAbsent(key, k -> new HashMap<>())
                    .put(date, new AttendanceCellDto(att.getId(), att.getAttendanceStatus()));
        }

        // 5. BUILD FINAL RESULT (ROWS ON THE CURRENT PAGE, IN DB ORDER)
        LocalDate currentPeriod = YearMonth.now().atDay(1);
        Map<Long, StudentBalanceResponse> balanceCache = new HashMap<>();
        List<AttendanceMatrixDto> result = new ArrayList<>();

        for (StudentGroupIdProjection pair : pairs) {
            Student student = studentsById.get(pair.getStudentId());
            Grouping group = groupsById.get(pair.getGroupId());
            if (student == null || group == null) {
                continue; // deleted/renamed between the pair query and the batch fetch
            }

            AttendanceMatrixDto dto = new AttendanceMatrixDto();

            dto.setStudentId(student.getId());
            dto.setStudentName(student.getFullName());
            dto.setGroupId(group.getId());
            dto.setGroupName(group.getName());
            dto.setPhone(student.getPhoneNumber());
            dto.setParentName(student.getParentName());
            dto.setParentPhone(student.getParentContact());

            boolean onTrial = invoiceGenerationService.isStillOnTrial(student.getId(), group, currentPeriod);
            dto.setStudentType(onTrial ? StudentType.TRIAL : StudentType.REGULAR);

            StudentBalanceResponse balance = balanceCache.computeIfAbsent(
                    student.getId(), paymentService::getStudentBalance);
            dto.setBalance(balance.getTotalCredit());
            dto.setDebt(balance.getTotalOwed());

            dto.setAttendance(
                    attendanceMap.getOrDefault(attendanceKey(student.getId(), group.getId()), new HashMap<>())
            );

            result.add(dto);
        }

        return result;
    }

    private static String attendanceKey(Long studentId, UUID groupId) {
        return studentId + "_" + groupId;
    }
}
