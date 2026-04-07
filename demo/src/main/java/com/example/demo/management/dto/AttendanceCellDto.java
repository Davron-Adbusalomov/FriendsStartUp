package com.example.demo.management.dto;

import com.example.demo.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceCellDto {
    private UUID id; // The UUID for the update API
    private AttendanceStatus status;
}
