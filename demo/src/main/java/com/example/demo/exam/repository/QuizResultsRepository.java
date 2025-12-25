package com.example.demo.exam.repository;

import com.example.demo.exam.model.QuizResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizResultsRepository extends JpaRepository<QuizResults, UUID> {

    List<QuizResults> findByStudentId(Long studentId);

    QuizResults findByStudentIdAndQuizId(Long studentId, UUID quizId);

}
