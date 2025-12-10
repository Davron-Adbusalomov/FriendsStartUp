package com.example.demo.management.specification;

import com.example.demo.enums.EnrollmentStatus;
import com.example.demo.management.model.Enrollment;
import org.springframework.data.jpa.domain.Specification;

public class EnrollmentSpecification {
    public static Specification<Enrollment> hasStatus(EnrollmentStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) return null;

            return criteriaBuilder.equal(root.get("enrollmentStatus"), status);
        };
    }

    public static Specification<Enrollment> hasStudentId(Long studentId) {
        return (root, query, criteriaBuilder) -> {
            if (studentId == null) return null;

            return criteriaBuilder.equal(root.get("studentId"), studentId);
        };
    }

    public static Specification<Enrollment> advancedFilter(EnrollmentStatus status, Long studentId) {
        return Specification
                .where(hasStatus(status))
                .and(hasStudentId(studentId));
    }
}
