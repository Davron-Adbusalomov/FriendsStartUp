package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.EnrollmentStatus;
import com.example.demo.management.dto.EnrollmentDTO;
import com.example.demo.management.mapper.EnrollmentMapper;
import com.example.demo.management.model.Enrollment;
import com.example.demo.management.repository.EnrollmentRepository;
import com.example.demo.management.specification.EnrollmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;

    public void processEnrollment(EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = enrollmentMapper.toEntity(enrollmentDTO);
        enrollment.setCenterId(TenantContext.getCenterId());
        enrollmentRepository.save(enrollment);
    }

    public Page<EnrollmentDTO> getEnrollmentsByStatus(EnrollmentStatus status, Long studentId, Pageable pageable) {
        Specification<Enrollment> spec = EnrollmentSpecification.advancedFilter(status, studentId);

        Page<Enrollment> enrollments = enrollmentRepository.findAll(spec, pageable);
        return enrollments.map(enrollmentMapper::toDTO);
    }

}
