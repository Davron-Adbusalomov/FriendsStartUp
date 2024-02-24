package com.example.demo.test.repository;

import com.example.demo.test.model.Quiz_Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Quiz_ResultsRepository  extends JpaRepository<Quiz_Results, Long> {

    List<Quiz_Results> findByStudentId(Long studentId);

    Quiz_Results findByStudentIdAndQuizId(Long studentId, Long quizId);

}
