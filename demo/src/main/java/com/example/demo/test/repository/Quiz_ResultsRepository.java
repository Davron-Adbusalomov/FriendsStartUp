package com.example.demo.test.repository;

import com.example.demo.test.model.Quiz_Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Quiz_ResultsRepository  extends JpaRepository<Quiz_Results, Long> {
}
