package com.example.demo.management.repository;

import com.example.demo.management.dto.AttendanceSummaryDTO;
import com.example.demo.management.dto.projection.*;
import com.example.demo.management.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query(value = """
            SELECT
                COUNT(DISTINCT g.id) as totalClasses,
                COUNT(DISTINCT gs.student_id) as totalStudents,
                COUNT(DISTINCT CASE WHEN a.attendance_status = 'PRESENT' THEN a.id END) as totalPresent,
                COUNT(DISTINCT CASE WHEN a.attendance_status = 'ABSENT'  THEN a.id END) as totalAbsent
            FROM teacher t
            LEFT JOIN groups g ON g.teacher_id = t.id
            LEFT JOIN group_student gs ON gs.group_id = g.id
            LEFT JOIN attendance a ON a.student_id = gs.student_id
                AND a.group_id = g.id
            WHERE t.id = :teacherId
            """, nativeQuery = true)
    TeacherStatsProjection getStats(@Param("teacherId") Long teacherId);

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
    List<TeachingActivityProjection> getTeachingActivity(@Param("teacherId") Long teacherId);

    @Query(value = """
            SELECT
                q.title as quizTitle,
                g.name as groupName,
                q.start_time as quizDate,
                ROUND(
                    CAST(
                        AVG(
                            CAST(qr.mark AS numeric) /
                            NULLIF(
                                CAST((SELECT SUM(que.mark)
                                      FROM quiz_question qq
                                      JOIN question que ON qq.question_id = que.id
                                      WHERE qq.quiz_id = q.id) AS numeric), 0
                            )
                        ) * 100 AS numeric
                    ), 2
                ) as percentage
            FROM quiz q
            JOIN groups g ON g.course_id = q.course_id AND g.teacher_id = :teacherId
            LEFT JOIN quiz_results qr ON qr.quiz_id = q.id
                AND EXISTS (
                    SELECT 1 FROM group_student gs
                    WHERE gs.group_id = g.id AND gs.student_id = qr.student_id
                )
            WHERE q.teacher_id = :teacherId
            GROUP BY q.id, q.title, g.name, q.start_time
            ORDER BY q.start_time
            """, nativeQuery = true)
    List<PerformanceProjection> getQuizPerformanceData(@Param("teacherId") Long teacherId);

    @Query(value = """
            SELECT
                g.name as title,
                -- Fix: Cast String to TIME before using TO_CHAR, or just select g.time
                g.time as time,
                null as room
            FROM groups g
            WHERE g.teacher_id = :teacherId
            ORDER BY g.time
            LIMIT 10
            """, nativeQuery = true)
    List<AgendaProjection> getAgenda(@Param("teacherId") Long teacherId);

    @Query(value = """
            SELECT
                CASE
                    WHEN COUNT(a.id) FILTER (WHERE a.attendance_status IN ('PRESENT','ABSENT','LATE')) = 0 THEN 0
                    ELSE CAST(ROUND(
                        COUNT(a.id) FILTER (WHERE a.attendance_status = 'PRESENT') * 100.0
                        / COUNT(a.id) FILTER (WHERE a.attendance_status IN ('PRESENT','ABSENT','LATE'))
                    ) AS integer)
                END as presentPercentage,
                CASE
                    WHEN COUNT(a.id) FILTER (WHERE a.attendance_status IN ('PRESENT','ABSENT','LATE')) = 0 THEN 0
                    ELSE CAST(ROUND(
                        COUNT(a.id) FILTER (WHERE a.attendance_status = 'ABSENT') * 100.0
                        / COUNT(a.id) FILTER (WHERE a.attendance_status IN ('PRESENT','ABSENT','LATE'))
                    ) AS integer)
                END as absentPercentage,
                CASE
                    WHEN COUNT(a.id) FILTER (WHERE a.attendance_status IN ('PRESENT','ABSENT','LATE')) = 0 THEN 0
                    ELSE CAST(ROUND(
                        COUNT(a.id) FILTER (WHERE a.attendance_status = 'LATE') * 100.0
                        / COUNT(a.id) FILTER (WHERE a.attendance_status IN ('PRESENT','ABSENT','LATE'))
                    ) AS integer)
                END as latePercentage
            FROM groups g
            LEFT JOIN group_student gs ON gs.group_id = g.id
            LEFT JOIN attendance a ON a.student_id = gs.student_id
                AND a.group_id = g.id
                AND EXTRACT(MONTH FROM a.attendance_time) = :month
                AND EXTRACT(YEAR  FROM a.attendance_time) = :year
            WHERE g.teacher_id = :teacherId AND g.id = CAST(:groupId AS uuid)
            """, nativeQuery = true)
    AttendanceSummaryProjection findAttendanceByGroupAndMonth(
            @Param("teacherId") Long teacherId,
            @Param("groupId") String groupId,
            @Param("month") int month,
            @Param("year") int year
    );
}