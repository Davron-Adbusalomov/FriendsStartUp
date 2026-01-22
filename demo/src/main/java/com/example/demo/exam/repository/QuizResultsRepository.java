package com.example.demo.exam.repository;

import com.example.demo.exam.interfaces.StudentRanking;
import com.example.demo.exam.model.QuizResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizResultsRepository extends JpaRepository<QuizResults, UUID> {

    List<QuizResults> findByStudentId(Long studentId);

    Optional<QuizResults> findByStudentIdAndQuizId(Long studentId, UUID quizId);

    @Query(value = """
        SELECT
            s.id AS studentId,
            s.full_name AS studentName,
            COALESCE(SUM(sr.mark), 0) AS totalMark,
            DENSE_RANK() OVER (
                ORDER BY COALESCE(SUM(sr.mark), 0) DESC
            ) AS rank
        FROM student s
        LEFT JOIN group_student gs
            ON gs.student_id = s.id
        LEFT JOIN quiz_results sr
            ON sr.student_id = s.id
            AND (CAST(:quizId AS UUID) IS NULL OR sr.quiz_id = :quizId)
        WHERE
            (
                CAST(:groupingId AS UUID) IS NOT NULL
                AND gs.group_id = :groupingId
            )
            OR
            (
                CAST(:groupingId AS UUID) IS NULL
                AND sr.quiz_id IS NOT NULL
            )
        GROUP BY s.id, s.full_name
        ORDER BY totalMark DESC
    """, nativeQuery = true)
    List<StudentRanking> findRankings(
            @Param("groupingId") UUID groupingId,
            @Param("quizId") UUID quizId
    );





}
