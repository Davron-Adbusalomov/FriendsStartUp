package com.example.demo.management.repository;

import com.example.demo.management.dto.projection.*;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    @Query("SELECT s FROM Student s WHERE s.parentContact = :parentContact")
    Optional<Student> findStudentByParentContact(@Param("parentContact") String parentContact);

    @Query("SELECT g FROM Grouping g JOIN g.students s WHERE s.id = :groupId")
    List<Grouping> findByGroupId(@Param("groupId") UUID groupId);

    @Query("SELECT s FROM Student s JOIN s.groupings g WHERE g.id = :groupId")
    List<Student> findAllByGroupId(UUID groupId);

    @Query(value = """
            SELECT
                COUNT(DISTINCT gs.group_id)                                                  AS totalGroups,
                COUNT(DISTINCT CASE WHEN lp.completed = true THEN l.id END)                  AS completedLessons,
                COUNT(DISTINCT l.id)                                                         AS totalLessons,
                (SELECT COUNT(*) FROM student_badge sb WHERE sb.student_id = :studentId)        AS totalBadges
            FROM student s
            LEFT JOIN group_student gs ON gs.student_id = s.id
            LEFT JOIN lesson l  ON l.group_id = gs.group_id AND l.status != 'DELETED'
            LEFT JOIN lesson_progress lp ON lp.lesson_id = l.id AND lp.student_id = s.id
            WHERE s.id = :studentId
            """, nativeQuery = true)
    StudentStatsProjection getDashboardStats(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT
                q.title                                                     AS quizTitle,
                COALESCE(g.name, c.name, 'N/A')                            AS groupName,
                qr.mark                                                     AS score,
                (SELECT COALESCE(SUM(que.mark), 0)
                 FROM student_answer sa
                 JOIN question que ON sa.question_id = que.id
                 WHERE sa.quiz_id = q.id AND sa.student_id = qr.student_id) AS totalPoints,
                LEAST(ROUND(
                    CAST(qr.mark AS numeric) /
                    NULLIF((SELECT SUM(que.mark)
                            FROM student_answer sa
                            JOIN question que ON sa.question_id = que.id
                            WHERE sa.quiz_id = q.id AND sa.student_id = qr.student_id), 0)
                    * 100, 2), 100)                                         AS percentage,
                q.start_time                                                AS quizDate
            FROM quiz_results qr
            JOIN quiz        q ON qr.quiz_id    = q.id
            LEFT JOIN groups g ON q.grouping_id = g.id
            LEFT JOIN course c ON q.course_id   = c.id
            WHERE qr.student_id = :studentId AND qr.status != 'DELETED'
            ORDER BY q.start_time DESC
            """, nativeQuery = true)
    List<StudentQuizPerformanceProjection> getQuizPerformance(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT
                g.name                                                                       AS groupName,
                COUNT(DISTINCT CASE WHEN lp.completed = true THEN l.id END)                  AS completedLessons,
                COUNT(DISTINCT l.id)                                                         AS totalLessons
            FROM group_student gs
            JOIN groups g   ON gs.group_id  = g.id  AND g.status != 'DELETED'
            LEFT JOIN course c  ON c.id         = g.course_id
            LEFT JOIN lesson l  ON l.course_id  = c.id AND l.status != 'DELETED'
            LEFT JOIN lesson_progress lp ON lp.lesson_id = l.id AND lp.student_id = :studentId
            WHERE gs.student_id = :studentId
            GROUP BY g.id, g.name
            """, nativeQuery = true)
    List<StudentGroupLessonProgressProjection> getLessonProgress(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT
                q.title       AS title,
                'QUIZ'        AS type,
                g.name        AS groupName,
                q.start_time  AS scheduledTime
            FROM quiz q
            JOIN groups g       ON q.grouping_id = g.id
            JOIN group_student gs ON gs.group_id = g.id
            WHERE gs.student_id = :studentId
              AND q.start_time > NOW()
              AND q.status != 'DELETED'
            ORDER BY q.start_time ASC
            LIMIT 5
            """, nativeQuery = true)
    List<StudentUpcomingTaskProjection> getUpcomingTasks(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT
                CASE WHEN COUNT(a.id) = 0 THEN 0
                     ELSE CAST(ROUND(
                         COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) * 100.0
                         / COUNT(a.id)) AS integer)
                END AS presentPercentage,
                CASE WHEN COUNT(a.id) = 0 THEN 0
                     ELSE CAST(ROUND(
                         COUNT(CASE WHEN a.attendance_status = 'ABSENT' THEN 1 END) * 100.0
                         / COUNT(a.id)) AS integer)
                END AS absentPercentage
            FROM attendance a
            WHERE a.student_id = :studentId
              AND EXTRACT(MONTH FROM a.attendance_time) = :month
              AND EXTRACT(YEAR  FROM a.attendance_time) = :year
              AND a.status != 'DELETED'
            """, nativeQuery = true)
    AttendanceSummaryProjection getAttendanceSummary(
            @Param("studentId") Long studentId,
            @Param("month") int month,
            @Param("year") int year);
}
