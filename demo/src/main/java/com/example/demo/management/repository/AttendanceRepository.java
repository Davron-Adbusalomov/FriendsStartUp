package com.example.demo.management.repository;

import com.example.demo.management.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Attendance> {
    @Query("""
       SELECT a FROM Attendance a 
       WHERE a.student.id = :studentId 
         AND a.attendanceTime BETWEEN :startOfDay AND :endOfDay
       ORDER BY a.attendanceTime DESC
       """)
    List<Attendance> findAllByStudentIdAndAttendanceTimeBetweenOrderByAttendanceTimeDesc(
            Long studentId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

}
