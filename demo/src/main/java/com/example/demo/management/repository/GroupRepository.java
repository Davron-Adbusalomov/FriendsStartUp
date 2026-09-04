package com.example.demo.management.repository;

import com.example.demo.management.dto.projection.StudentGroupIdProjection;
import com.example.demo.management.model.Grouping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<Grouping> findByCenterId(UUID centerId);

    @Query("SELECT g FROM Grouping g JOIN g.students s WHERE s.id = :studentId")
    List<Grouping> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT g FROM Grouping g JOIN g.students s WHERE s.id = :studentId AND g.courseId = :courseId")
    List<Grouping> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") UUID courseId);

    @EntityGraph(attributePaths = "teacher")
    @Query("select g from Grouping g where g.id = :groupId")
    Optional<Grouping> findByIdWithTeacher(UUID groupId);

    /**
     * Paginated (studentId, groupId) pairs for the attendance matrix, filtered directly
     * in the database (center-scoped, optionally by teacher and/or a single group) — avoids
     * loading every student of every matched group into memory just to paginate in Java.
     */
    @Query(
            value = """
                    SELECT gs.student_id AS studentId, gs.group_id AS groupId
                    FROM group_student gs
                    JOIN groups  g ON g.id = gs.group_id
                    JOIN student st ON st.id = gs.student_id
                    WHERE g.status != 'DELETED'
                      AND st.status != 'DELETED'
                      AND g.center_id = :centerId
                      AND (:teacherId IS NULL OR g.teacher_id = :teacherId)
                      AND (CAST(:groupId AS UUID) IS NULL OR g.id = :groupId)
                    ORDER BY g.name, st.full_name
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM group_student gs
                    JOIN groups  g ON g.id = gs.group_id
                    JOIN student st ON st.id = gs.student_id
                    WHERE g.status != 'DELETED'
                      AND st.status != 'DELETED'
                      AND g.center_id = :centerId
                      AND (:teacherId IS NULL OR g.teacher_id = :teacherId)
                      AND (CAST(:groupId AS UUID) IS NULL OR g.id = :groupId)
                    """,
            nativeQuery = true
    )
    Page<StudentGroupIdProjection> findStudentGroupPairs(
            @Param("centerId") UUID centerId,
            @Param("teacherId") Long teacherId,
            @Param("groupId") UUID groupId,
            Pageable pageable
    );
}
