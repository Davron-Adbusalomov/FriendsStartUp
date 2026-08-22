package com.example.demo.management.repository;

import com.example.demo.enums.EnrollmentStatus;
import com.example.demo.management.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>, JpaSpecificationExecutor<Enrollment> {

    List<Enrollment> findByCenterIdAndEnrollmentStatus(UUID centerId, EnrollmentStatus enrollmentStatus);

    Optional<Enrollment> findByStudentIdAndGroupIdAndEnrollmentStatus(Long studentId, UUID groupId, EnrollmentStatus enrollmentStatus);
}
