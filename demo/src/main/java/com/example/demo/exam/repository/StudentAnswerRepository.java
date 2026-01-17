package com.example.demo.exam.repository;

import com.example.demo.exam.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, UUID> {
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.studentId = :studentId AND sa.quizId = :quizId AND sa.questionId = :id")
    Optional<StudentAnswer> findByStudentIdAndQuizIdAndQuestionId(Long studentId, UUID quizId, UUID id);

    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.quizId = :quizId AND sa.studentId = :studentId")
    List<StudentAnswer> findByQuizIdAndStudentId(UUID quizId, Long studentId);

    @Query("""
    SELECT sa, q
    FROM StudentAnswer sa
    JOIN Question q ON q.id = sa.questionId
    WHERE sa.quizId = :quizId
      AND q.type = 'WRITTEN'
      AND sa.correct IS NULL
      AND sa.score IS NULL
    """)
    List<Object[]> findNotEvaluatedWrittenAnswersByQuizId(UUID quizId);


}
