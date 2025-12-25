package com.example.demo.management.specification;

import com.example.demo.exam.model.Question;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class QuestionSpecification {

    public static Specification<Question> hasDifficultyLevel(String level) {
        return (root, query, cb) ->
                level == null ? null : cb.equal(root.get("level"), level);
    }

    public static Specification<Question> hasTeacherId(Long teacherId) {
        return (root, query, cb) ->
                teacherId == null ? null : cb.equal(root.get("teacherId"), teacherId);
    }

    public static Specification<Question> hasGroupQuizId(UUID quizId) {
        return (root, query, cb) ->
                quizId == null ? null : cb.equal(root.get("quizId"), quizId);
    }

    private static Specification<Question> hasSubjectId(UUID subjectId) {
        return (root, query, cb) ->
                subjectId == null ? null : cb.equal(root.get("subjectId"), subjectId);
    }

    private static Specification<Question> hasTitleLike(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Question> advancedFilter(String level, Long teacherId, UUID quizId, String title, UUID subjectId) {
        return Specification
                .where(hasDifficultyLevel(level))
                .and(hasTeacherId(teacherId))
                .and(hasGroupQuizId(quizId))
                .and(hasTitleLike(title)
                .and(hasSubjectId(subjectId)));
    }
}
