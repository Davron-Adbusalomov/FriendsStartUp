package com.example.demo.exam.repository;

import com.example.demo.exam.model.WrittenQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WrittenQuestionsRepository extends JpaRepository<WrittenQuestions, UUID> {
    List<WrittenQuestions> findByQuizId(UUID quizId);

}
