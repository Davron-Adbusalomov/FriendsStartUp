package com.example.demo.exam.repository;

import com.example.demo.exam.model.WrongAnswersAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WrongAnswersAnalyzeRepository extends JpaRepository<WrongAnswersAnalyze, Long> {

}
