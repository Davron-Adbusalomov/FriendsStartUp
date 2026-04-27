package com.example.demo.management.repository;

import com.example.demo.management.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {

    @Query("SELECT lp FROM LessonProgress lp WHERE lp.studentId = :studentId")
    List<LessonProgress> findLessonProgressByStudentId(Long studentId);

    @Query("SELECT lp FROM LessonProgress lp WHERE lp.lesson.id = :lessonId AND lp.studentId = :studentId")
    Optional<LessonProgress> findByLessonIdAndStudentId(UUID lessonId, Long studentId);

    @Query("""
            SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.lesson.groupId = :groupId
            """)
    long countByGroupId(UUID groupId);

    @Query("""
            SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.lesson.groupId = :groupId AND lp.studentId = :studentId AND lp.completed = true
            """)
    long countByGroupIdAndStudentIdAndCompletedTrue(
            UUID groupId,
            Long studentId
    );
}
