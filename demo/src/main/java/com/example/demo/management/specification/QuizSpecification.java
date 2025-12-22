package com.example.demo.management.specification;

import com.example.demo.enums.QuizContentStatus;
import com.example.demo.exam.model.Quiz;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuizSpecification {
    public static Specification<Quiz> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Quiz> hasGroupId(UUID groupId) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("groupingId"), groupId);
    }

    public static Specification<Quiz> hasStatus(QuizContentStatus status) {
        return (root, query, cb) -> {

            if (status == null) {
                return null;
            }

            LocalDateTime now = LocalDateTime.now();

            if (status == QuizContentStatus.PENDING) {
                return cb.greaterThan(root.get("startTime"), now);
            } else if (status == QuizContentStatus.ONGOING) {
                return cb.and(
                        cb.lessThanOrEqualTo(root.get("startTime"), now),
                        cb.greaterThanOrEqualTo(root.get("endTime"), now)
                );
            } else if (status == QuizContentStatus.COMPLETED) {
                return cb.lessThan(root.get("endTime"), now);
            }
            return null;
        };
    }

    public static Specification<Quiz> advancedFilter(UUID groupId, String title, QuizContentStatus status) {
        return Specification.where(title != null ? hasTitle(title) : null)
                .and(groupId != null ? hasGroupId(groupId) : null)
                .and(status != null ? hasStatus(status) : null);
    }
}
