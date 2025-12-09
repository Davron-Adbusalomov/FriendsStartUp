package com.example.demo.management.specification;

import com.example.demo.enums.GroupStatus;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class GroupSpecification {
    public static Specification<Grouping> teacherIdEquals(Long teacherId) {
        return (root, query, criteriaBuilder) -> teacherId == null ? null :
                criteriaBuilder.equal(root.get("teacherId"), teacherId);
    }

    public static Specification<Grouping> studentIdEquals(Long studentId) {
        return (root, query, cb) -> {
            if (studentId == null) return null;

            query.distinct(true);

            Join<Grouping, Student> studentsJoin = root.join("students");

            return cb.equal(studentsJoin.get("id"), studentId);
        };
    }


    public static Specification<Grouping> nameContains(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null :
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Grouping> statusEquals(String status) {
        if (status == null) return null;
        if (status.equalsIgnoreCase(GroupStatus.ONGOING.name())) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("startDate"), criteriaBuilder.currentDate());
        } else if (status.equalsIgnoreCase(GroupStatus.NOT_STARTED.name())) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), criteriaBuilder.currentDate());
        } else {
            return null;
        }
    }

    public static Specification<Grouping> advancedFilter(Long teacherId, Long studentId, String name, String status) {
        return Specification
                .where(teacherIdEquals(teacherId))
                .and(studentIdEquals(studentId))
                .and(nameContains(name))
                .and(statusEquals(status));
    }
}
