package com.example.demo.test.repository;

import com.example.demo.test.model.WrittenQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WrittenQuestionsRepository extends JpaRepository<WrittenQuestions, Long> {
    List<WrittenQuestions> findByQuizId(Long quizId);
}
