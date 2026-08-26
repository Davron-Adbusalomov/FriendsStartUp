package com.example.demo.management.dto;

import com.example.demo.enums.StudentType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class AttendanceMatrixDto {
    private Long studentId;
    private String studentName;

    private String phone;
    private String parentName;
    private String parentPhone;
    private StudentType studentType;
    private BigDecimal balance;
    private BigDecimal debt;

    private Map<String, AttendanceCellDto> attendance;
}
