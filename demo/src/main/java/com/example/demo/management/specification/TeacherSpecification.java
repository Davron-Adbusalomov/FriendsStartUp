package com.example.demo.management.specification;

import com.example.demo.management.model.Teacher;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TeacherSpecification {
    public static Specification<Teacher> hasCenterId(UUID centerId) {
        return (root, query, cb) ->
                centerId == null ? null : cb.equal(root.get("centerId"), centerId);
    }

    public static Specification<Teacher> advancedFilter(UUID centerId) {
        return Specification.where(hasCenterId(centerId));
    }
}
