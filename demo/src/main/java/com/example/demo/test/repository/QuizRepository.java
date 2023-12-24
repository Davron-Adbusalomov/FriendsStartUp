package com.example.demo.test.repository;

import com.example.demo.management.model.Grouping;
import com.example.demo.test.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :quizId")
    Optional<Quiz> findByIdWithQuestions(@Param("quizId") Long quizId);

    Optional<Quiz> findById(Long id);

    List<Quiz> findByTeacherId(Long id);

    List<Quiz> findByGroupingId(Long groupId);

    @Query("select q from Quiz q JOIN q.questions qu where qu.id = :questionId")
    List<Quiz> findByQuestionId(@Param("questionId") Long questionId);

}
