package com.example.demo.management.dto.request;
import com.example.demo.enums.AttendanceStatus;
import com.example.demo.management.dto.StudentAttendanceStatusDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AttendanceCreateRequest {
    private UUID groupId;
    private LocalDateTime attendanceTime;
    private List<StudentAttendanceStatusDto> studentStatuses; // Changed this
}
