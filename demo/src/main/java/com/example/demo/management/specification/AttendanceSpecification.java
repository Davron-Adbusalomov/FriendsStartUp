package com.example.demo.management.specification;

import com.example.demo.management.model.Attendance;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AttendanceSpecification {

    public static Specification<Attendance> hasStudentName(String fullName) {
        return (root, query, cb) -> {
            if (fullName == null || fullName.isEmpty())
                return cb.conjunction();

            Join<Object, Object> studentJoin = root.join("student");
            return cb.like(cb.lower(studentJoin.get("fullName")), "%" + fullName.toLowerCase() + "%");
        };
    }

    public static Specification<Attendance> hasStudentId(Long studentId) {
        return (root, query, cb) -> {
            if (studentId == null)
                return cb.conjunction();

            Join<Object, Object> studentJoin = root.join("student");
            return cb.equal(studentJoin.get("id"), studentId);
        };
    }

    public static Specification<Attendance> hasGroupName(String groupName) {
        return (root, query, cb) -> {
            if (groupName == null || groupName.isEmpty())
                return cb.conjunction();

            Join<Object, Object> groupJoin = root.join("grouping");
            return cb.like(cb.lower(groupJoin.get("name")), "%" + groupName.toLowerCase() + "%");
        };
    }

    public static Specification<Attendance> dateBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null)
                return cb.conjunction();

            if (from != null && to != null)
                return cb.between(root.get("attendanceTime"), from, to);

            if (from != null)
                return cb.greaterThanOrEqualTo(root.get("attendanceTime"), from);

            return cb.lessThanOrEqualTo(root.get("attendanceTime"), to);
        };
    }
}
