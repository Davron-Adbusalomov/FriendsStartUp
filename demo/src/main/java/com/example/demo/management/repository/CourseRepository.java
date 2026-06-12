package com.example.demo.management.repository;

import com.example.demo.management.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);
    Page<Course> findByCenterId(UUID centerId, Pageable pageable);
    Page<Course> findByTeacherIdAndCenterId(Long teacherId, UUID centerId, Pageable pageable);
}
