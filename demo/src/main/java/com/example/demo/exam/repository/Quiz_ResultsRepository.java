package com.example.demo.exam.repository;

import com.example.demo.exam.model.Quiz_Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface Quiz_ResultsRepository  extends JpaRepository<Quiz_Results, UUID> {

    List<Quiz_Results> findByStudentId(Long studentId);

    Quiz_Results findByStudentIdAndQuizId(Long studentId, UUID quizId);

}
