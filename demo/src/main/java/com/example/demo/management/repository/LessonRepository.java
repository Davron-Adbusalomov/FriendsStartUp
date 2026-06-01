package com.example.demo.management.repository;

import com.example.demo.management.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID>, JpaSpecificationExecutor<Lesson> {

    @Query("SELECT l FROM Lesson l WHERE l.courseId = :courseId ORDER BY l.orderIndex ASC")
    List<Lesson> findAllByCourseIdOrderByOrderIndexAsc(UUID courseId);
}
