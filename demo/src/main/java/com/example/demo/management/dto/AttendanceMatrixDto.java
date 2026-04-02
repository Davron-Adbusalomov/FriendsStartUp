package com.example.demo.management.dto;

import com.example.demo.enums.AttendanceStatus;
import lombok.Data;

import java.util.Map;

@Data
public class AttendanceMatrixDto {

    private Long studentId;
    private String studentName;

    // key = date (yyyy-MM-dd), value = status
    private Map<String, AttendanceStatus> attendance;
}