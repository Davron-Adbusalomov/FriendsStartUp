package com.example.demo.management.dto;

import com.example.demo.enums.AttendanceStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AttendanceDto {
    private UUID id;

    private Long studentId;

    private StudentDTO student;

    private String groupId;

    private GroupDTO grouping;

    private LocalDateTime attendanceTime;

    private AttendanceStatus attendanceStatus;
}
