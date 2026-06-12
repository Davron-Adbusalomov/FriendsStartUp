package com.example.demo.management.specification;

import com.example.demo.management.model.Subject;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class SubjectSpecification {
    public static Specification<Subject> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Subject> hasGroupId(String code) {
        return (root, query, cb) ->
                code == null ? null : cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%");
    }

    public static Specification<Subject> hasCenterId(UUID centerId) {
        return (root, query, cb) ->
                centerId == null ? null : cb.equal(root.get("centerId"), centerId);
    }

    public static Specification<Subject> advancedFilter(String name, String code, UUID centerId) {
        return Specification
                .where(hasName(name))
                .and(hasGroupId(code))
                .and(hasCenterId(centerId));
    }
}
