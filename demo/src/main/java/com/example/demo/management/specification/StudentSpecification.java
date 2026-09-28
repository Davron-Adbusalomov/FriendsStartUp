package com.example.demo.management.specification;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class StudentSpecification {
    public static Specification<Student> hasGroupId(UUID groupId) {
        return (root, query, cb) ->
        {
            if (groupId == null) return null;

            query.distinct(true);

            Join<Student, Student> studentsJoin = root.join("groupings");

            return cb.equal(studentsJoin.get("id"), groupId);
        };
    }

    public static Specification<Student> hasCenterId(UUID centerId) {
        return (root, query, cb) ->
                centerId == null ? null : cb.equal(root.get("centerId"), centerId);
    }

    public static Specification<Student> hasSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String value = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), value),
                    cb.like(cb.lower(root.get("phoneNumber")), value)
            );
        };
    }

    public static Specification<Student> advancedFilter(
            UUID groupId,
            String search,
            UUID centerId
    ) {
        return Specification
                .where(hasGroupId(groupId))
                .and(hasCenterId(centerId))
                .and(hasSearch(search));
    }
}
