package com.example.demo.management.repository;

import com.example.demo.management.model.Teacher;
import com.example.demo.management.dto.projection.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query(value = """
            SELECT
                COUNT(DISTINCT g.id) as totalClasses,

                COUNT(DISTINCT gs.student_id) as totalStudents,

                COUNT(DISTINCT CASE
                    WHEN a.status = 'PRESENT'
                    THEN a.id
                END) as totalPresent,

                COUNT(DISTINCT CASE
                    WHEN a.status = 'ABSENT'
                    THEN a.id
                END) as totalAbsent

            FROM teacher t

            LEFT JOIN groups g
                ON g.teacher_id = t.id

            LEFT JOIN group_student gs
                ON gs.group_id = g.id

            LEFT JOIN attendance a
                ON a.student_id = gs.student_id

            WHERE t.id = :teacherId
            """, nativeQuery = true)
    TeacherStatsProjection getStats(Long teacherId);

    @Query(value = """
            SELECT
                TO_CHAR(g.created_at, 'Mon') as month,
                COUNT(g.id) as classes

            FROM groups g

            WHERE g.teacher_id = :teacherId

            GROUP BY
                TO_CHAR(g.created_at, 'Mon'),
                DATE_TRUNC('month', g.created_at)

            ORDER BY DATE_TRUNC('month', g.created_at)
            """, nativeQuery = true)
    List<TeachingActivityProjection> getTeachingActivity(Long teacherId);

    @Query(value = """
            SELECT
                'Mon' as day,
                75 as assignment,
                85 as midterm,
                65 as finalExam

            UNION ALL

            SELECT 'Tue', 60, 70, 80

            UNION ALL

            SELECT 'Wed', 80, 75, 70

            UNION ALL

            SELECT 'Thu', 70, 90, 75

            UNION ALL

            SELECT 'Fri', 85, 65, 85
            """, nativeQuery = true)
    List<PerformanceProjection> getPerformance();

    @Query(value = """
            SELECT
                g.name as title,
                TO_CHAR(g.time, 'HH24:MI') as time,
                g.room as room

            FROM groups g

            WHERE g.teacher_id = :teacherId

            ORDER BY g.time

            LIMIT 10
            """, nativeQuery = true)
    List<AgendaProjection> getAgenda(Long teacherId);
}