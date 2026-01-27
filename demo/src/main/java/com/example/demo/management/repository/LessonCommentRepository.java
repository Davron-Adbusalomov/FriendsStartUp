package com.example.demo.management.repository;

import com.example.demo.management.model.LessonComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface LessonCommentRepository extends JpaRepository<LessonComment, UUID> {
    List<LessonComment> findAllByLessonId(UUID lessonId);
}
