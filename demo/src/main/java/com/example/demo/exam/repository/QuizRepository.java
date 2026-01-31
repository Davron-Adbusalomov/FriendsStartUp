package com.example.demo.exam.repository;

import com.example.demo.exam.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID>, JpaSpecificationExecutor<Quiz> {
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :quizId")
    Optional<Quiz> findByIdWithQuestions(@Param("quizId") Long quizId);

    Optional<Quiz> findById(UUID id);

    List<Quiz> findByTeacher_Id(Long id);

    List<Quiz> findByGroupingId(UUID groupId);

    @Query("select q from Quiz q JOIN q.questions qu where qu.id = :questionId")
    List<Quiz> findByQuestionId(@Param("questionId") UUID questionId);

    @Query("SELECT q FROM Quiz q WHERE q.groupingId IN :groupIds AND q.startTime > :now ORDER BY q.startTime ASC")
    Quiz findUpcomingQuizByGroupIds(@Param("groupIds") List<UUID> list, @Param("now") LocalDateTime now);

    @Query("""
    select q from Quiz q
    left join fetch q.questions ques
    where q.id = :quizId
""")
    Optional<Quiz> findByIdWithQuestions(UUID quizId);

}
