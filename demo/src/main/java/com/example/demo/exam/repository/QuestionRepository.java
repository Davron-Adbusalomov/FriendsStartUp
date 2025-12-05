package com.example.demo.exam.repository;

import com.example.demo.exam.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Question> {

    List<Question> findByTeacherId(Long id);

    @Query("SELECT q FROM Question q WHERE q.level = :level")
    List<Question> findQuestionByLevel(String level);

    Optional<Question> findById(UUID question_id);

}
