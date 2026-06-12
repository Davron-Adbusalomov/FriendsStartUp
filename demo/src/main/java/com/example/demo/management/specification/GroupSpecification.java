package com.example.demo.management.specification;

import com.example.demo.enums.GroupStatus;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

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


    public static Specification<Grouping> statusEquals(String status, Long studentId) {

        if (status == null) return null;

        if (status.equalsIgnoreCase(GroupStatus.ONGOING.name())) {
            return (root, query, cb) ->
                    cb.lessThan(root.get("startDate"), cb.currentDate());
        }

        if (status.equalsIgnoreCase(GroupStatus.NOT_STARTED.name())) {
            return (root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startDate"), cb.currentDate());
        }

        if (status.equalsIgnoreCase(GroupStatus.NOT_JOINED.name())) {
            return (root, query, cb) -> {
                if (studentId == null) return null;

                query.distinct(true);

                var subquery = query.subquery(UUID.class);
                var subRoot = subquery.from(Grouping.class);
                var subJoin = subRoot.join("students");

                subquery.select(subRoot.get("id"))
                        .where(cb.equal(subJoin.get("id"), studentId));

                return cb.not(root.get("id").in(subquery));
            };
        }


        return null;
    }


    public static Specification<Grouping> hasCenterId(UUID centerId) {
        return (root, query, criteriaBuilder) -> centerId == null ? null :
                criteriaBuilder.equal(root.get("centerId"), centerId);
    }

    public static Specification<Grouping> advancedFilter(
            Long teacherId,
            Long studentId,
            String name,
            String status,
            UUID centerId
    ) {
        Specification<Grouping> spec = (root, query, cb) -> {
            query.distinct(true);
            return null;
        };

        spec = spec
                .and(teacherIdEquals(teacherId))
                .and(nameContains(name))
                .and(statusEquals(status, studentId))
                .and(hasCenterId(centerId));

        if (!GroupStatus.NOT_JOINED.name().equalsIgnoreCase(status)) {
            spec = spec.and(studentIdEquals(studentId));
        }

        return spec;
    }

}
