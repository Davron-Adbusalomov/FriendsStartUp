package com.example.demo.management.dto;

import com.example.demo.enums.AttendanceStatus;
import lombok.Data;

@Data
public class StudentAttendanceStatusDto {
    private Long studentId;
    private AttendanceStatus status;
}
