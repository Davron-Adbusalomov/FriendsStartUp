package com.example.demo.test.repository;

import com.example.demo.test.model.WrongAnswersAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WrongAnswersAnalyzeRepository extends JpaRepository<WrongAnswersAnalyze, Long> {

}
