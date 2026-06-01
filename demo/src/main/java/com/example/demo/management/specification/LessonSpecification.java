package com.example.demo.management.specification;

import com.example.demo.management.model.Lesson;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class LessonSpecification {

    public Specification<Lesson> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Lesson> hasCourseId(UUID courseId) {
        return (root, query, cb) ->
                courseId == null ? null : cb.equal(root.get("courseId"), courseId);
    }

    public static Specification<Lesson> advancedFilter(String title, UUID courseId) {
        return Specification
                .where(new LessonSpecification().hasTitle(title))
                .and(hasCourseId(courseId));
    }
}
