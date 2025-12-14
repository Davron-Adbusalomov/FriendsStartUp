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

    public static Specification<Student> advancedFilter(UUID groupId) {
        return Specification.where(hasGroupId(groupId));
    }
}
