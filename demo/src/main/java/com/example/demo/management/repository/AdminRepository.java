package com.example.demo.management.repository;

import com.example.demo.management.dto.projection.*;
import com.example.demo.management.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query(value = """
            SELECT
                (SELECT COUNT(*) FROM student  s WHERE s.center_id = :centerId AND s.status != 'DELETED') AS totalStudents,
                (SELECT COUNT(*) FROM teacher  t WHERE t.center_id = :centerId AND t.status != 'DELETED') AS totalTeachers,
                (SELECT COUNT(*) FROM groups   g WHERE g.center_id = :centerId AND g.status != 'DELETED') AS totalGroups,
                (SELECT COUNT(*) FROM badge    b WHERE b.center_id = :centerId AND b.status != 'DELETED') AS totalBadges
            """, nativeQuery = true)
    AdminStatsProjection getStats(@Param("centerId") UUID centerId);

    @Query(value = """
            SELECT
                TO_CHAR(a.attendance_time, 'Dy')                                                        AS dayOfWeek,
                CAST(COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) AS integer)           AS presentCount,
                CAST(COUNT(CASE WHEN a.attendance_status = 'ABSENT'  THEN 1 END) AS integer)           AS absentCount
            FROM attendance a
            WHERE a.center_id = :centerId
              AND a.attendance_time >= DATE_TRUNC('week', CURRENT_DATE)
              AND a.attendance_time <  DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '7 days'
              AND a.status != 'DELETED'
            GROUP BY TO_CHAR(a.attendance_time, 'Dy'), DATE_TRUNC('day', a.attendance_time)
            ORDER BY DATE_TRUNC('day', a.attendance_time)
            """, nativeQuery = true)
    List<DailyAttendanceProjection> getWeeklyAttendance(@Param("centerId") UUID centerId);

    @Query(value = """
            SELECT
                CASE WHEN COUNT(a.id) = 0 THEN 0
                     ELSE CAST(ROUND(COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) * 100.0
                                     / COUNT(a.id)) AS integer)
                END AS presentPercentage,
                CASE WHEN COUNT(a.id) = 0 THEN 0
                     ELSE CAST(ROUND(COUNT(CASE WHEN a.attendance_status = 'ABSENT' THEN 1 END) * 100.0
                                     / COUNT(a.id)) AS integer)
                END AS absentPercentage
            FROM attendance a
            WHERE a.center_id = :centerId
              AND EXTRACT(MONTH FROM a.attendance_time) = :month
              AND EXTRACT(YEAR  FROM a.attendance_time) = :year
              AND a.status != 'DELETED'
            """, nativeQuery = true)
    AttendanceSummaryProjection getAttendanceSummary(
            @Param("centerId") UUID centerId,
            @Param("month") int month,
            @Param("year") int year);

    @Query(value = """
            SELECT
                TO_CHAR(q.start_time, 'Mon')                                                    AS month,
                ROUND(AVG(
                    CAST(qr.mark AS numeric) /
                    NULLIF((SELECT SUM(que.mark)
                            FROM quiz_question qq
                            JOIN question que ON qq.question_id = que.id
                            WHERE qq.quiz_id = q.id), 0) * 100
                ), 1)                                                                           AS avgQuizScore,
                ROUND(
                    COUNT(DISTINCT CASE WHEN lp.completed = true THEN lp.id END) * 100.0 /
                    NULLIF(COUNT(DISTINCT l.id), 0)
                , 1)                                                                            AS lessonCompletionRate
            FROM groups g
            JOIN quiz q         ON q.grouping_id = g.id  AND q.status  != 'DELETED'
            LEFT JOIN quiz_results  qr ON qr.quiz_id  = q.id  AND qr.status != 'DELETED'
            LEFT JOIN lesson    l  ON l.group_id  = g.id  AND l.status  != 'DELETED'
            LEFT JOIN lesson_progress lp ON lp.lesson_id = l.id
            WHERE g.center_id = :centerId
              AND q.start_time >= NOW() - INTERVAL '12 months'
            GROUP BY TO_CHAR(q.start_time, 'Mon'), DATE_TRUNC('month', q.start_time)
            ORDER BY DATE_TRUNC('month', q.start_time)
            """, nativeQuery = true)
    List<MonthlyActivityProjection> getMonthlyActivity(@Param("centerId") UUID centerId);

    @Query(value = """
            SELECT
                g.name        AS title,
                g.time        AS time,
                null          AS room
            FROM groups g
            WHERE g.center_id = :centerId
              AND g.status != 'DELETED'
            ORDER BY g.time
            LIMIT 5
            """, nativeQuery = true)
    List<AgendaProjection> getAgenda(@Param("centerId") UUID centerId);

    @Query(value = """
            SELECT
                s.full_name  AS studentName,
                g.name       AS groupName,
                ROUND(
                    CAST(qr.mark AS numeric) /
                    NULLIF((SELECT SUM(que.mark)
                            FROM quiz_question qq
                            JOIN question que ON qq.question_id = que.id
                            WHERE qq.quiz_id = q.id), 0) * 100, 1
                )            AS score,
                ROW_NUMBER() OVER (ORDER BY qr.mark DESC) AS rank
            FROM quiz_results qr
            JOIN student s ON qr.student_id = s.id
            JOIN quiz    q ON qr.quiz_id    = q.id
            JOIN groups  g ON q.grouping_id = g.id
            WHERE g.center_id = :centerId
              AND qr.status != 'DELETED'
            ORDER BY qr.mark DESC
            LIMIT 5
            """, nativeQuery = true)
    List<TopPerformerProjection> getTopPerformers(@Param("centerId") UUID centerId);
}
