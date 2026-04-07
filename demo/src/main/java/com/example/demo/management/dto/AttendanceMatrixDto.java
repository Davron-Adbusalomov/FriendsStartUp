package com.example.demo.management.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AttendanceMatrixDto {
    private Long studentId;
    private String studentName;

    private Map<String, AttendanceCellDto> attendance;
}