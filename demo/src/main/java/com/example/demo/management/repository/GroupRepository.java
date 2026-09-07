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

import java.time.LocalDateTime;
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
     * <p>
     * {@code todayStatus} additionally narrows the result to students whose attendance
     * *for today* (in that group) matches the given status — {@code NOT_MARKED} means no
     * attendance row exists for today at all. Pass {@code null} to skip this filter.
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
                      AND (
                            :todayStatus IS NULL
                            OR (
                                 :todayStatus = 'NOT_MARKED' AND NOT EXISTS (
                                     SELECT 1 FROM attendance a
                                     WHERE a.student_id = gs.student_id
                                       AND a.group_id = gs.group_id
                                       AND a.status != 'DELETED'
                                       AND a.attendance_time BETWEEN :todayStart AND :todayEnd
                                 )
                               )
                            OR (
                                 :todayStatus <> 'NOT_MARKED' AND EXISTS (
                                     SELECT 1 FROM attendance a
                                     WHERE a.student_id = gs.student_id
                                       AND a.group_id = gs.group_id
                                       AND a.status != 'DELETED'
                                       AND a.attendance_time BETWEEN :todayStart AND :todayEnd
                                       AND a.attendance_status = :todayStatus
                                 )
                               )
                          )
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
                      AND (
                            :todayStatus IS NULL
                            OR (
                                 :todayStatus = 'NOT_MARKED' AND NOT EXISTS (
                                     SELECT 1 FROM attendance a
                                     WHERE a.student_id = gs.student_id
                                       AND a.group_id = gs.group_id
                                       AND a.status != 'DELETED'
                                       AND a.attendance_time BETWEEN :todayStart AND :todayEnd
                                 )
                               )
                            OR (
                                 :todayStatus <> 'NOT_MARKED' AND EXISTS (
                                     SELECT 1 FROM attendance a
                                     WHERE a.student_id = gs.student_id
                                       AND a.group_id = gs.group_id
                                       AND a.status != 'DELETED'
                                       AND a.attendance_time BETWEEN :todayStart AND :todayEnd
                                       AND a.attendance_status = :todayStatus
                                 )
                               )
                          )
                    """,
            nativeQuery = true
    )
    Page<StudentGroupIdProjection> findStudentGroupPairs(
            @Param("centerId") UUID centerId,
            @Param("teacherId") Long teacherId,
            @Param("groupId") UUID groupId,
            @Param("todayStatus") String todayStatus,
            @Param("todayStart") LocalDateTime todayStart,
            @Param("todayEnd") LocalDateTime todayEnd,
            Pageable pageable
    );
}
