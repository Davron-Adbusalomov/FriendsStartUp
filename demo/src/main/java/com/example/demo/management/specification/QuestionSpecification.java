package com.example.demo.management.specification;

import com.example.demo.exam.model.Question;
import com.example.demo.exam.model.Quiz;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
        return (root, query, cb) -> {
            if (quizId == null) return null;
            query.distinct(true);
            Join<Question, Quiz> quizJoin = root.join("quizzes", JoinType.INNER);
            return cb.equal(quizJoin.get("id"), quizId);
        };
    }

    private static Specification<Question> hasSubjectId(UUID subjectId) {
        return (root, query, cb) ->
                subjectId == null ? null : cb.equal(root.get("subjectId"), subjectId);
    }

    private static Specification<Question> hasTitleLike(String search) {
        return (root, query, cb) ->
                search == null ? null :
                        cb.or(
                                cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"),
                                cb.like(cb.lower(root.get("topic")), "%" + search.toLowerCase() + "%")
                        );
    }

    private static Specification<Question> hasCenterId(UUID centerId) {
        return (root, query, cb) ->
                centerId == null ? null : cb.equal(root.get("centerId"), centerId);
    }

    public static Specification<Question> advancedFilter(String level, Long teacherId, UUID quizId, String title, UUID subjectId, UUID centerId) {
        return Specification
                .where(hasCenterId(centerId))
                .and(hasDifficultyLevel(level))
                .and(hasTeacherId(teacherId))
                .and(hasGroupQuizId(quizId))
                .and(hasTitleLike(title))
                .and(hasSubjectId(subjectId));
    }
}
