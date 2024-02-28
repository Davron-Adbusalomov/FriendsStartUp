package com.example.demo.test.repository;

import com.example.demo.test.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTeacherId(Long id);

    @Query("SELECT q FROM Question q WHERE q.level = :level")
    List<Question> findQuestionByLevel(String level);

}
