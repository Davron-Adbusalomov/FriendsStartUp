package com.example.demo.management.repository;

import com.example.demo.management.model.Grouping;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Grouping, UUID>, JpaSpecificationExecutor<Grouping> {
    Optional<Grouping> findByName(String aLong);

    List<Grouping> findByTeacherId(Long teacherId);

    @Query("SELECT g FROM Grouping g JOIN g.students s WHERE s.id = :studentId")
    List<Grouping> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT g FROM Grouping g JOIN g.students s WHERE s.id = :studentId AND g.courseId = :courseId")
    List<Grouping> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") UUID courseId);

    @EntityGraph(attributePaths = "teacher")
    @Query("select g from Grouping g where g.id = :groupId")
    Optional<Grouping> findByIdWithTeacher(UUID groupId);


}
