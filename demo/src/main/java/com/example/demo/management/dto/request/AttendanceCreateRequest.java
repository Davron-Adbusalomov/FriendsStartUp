package com.example.demo.management.dto.request;
import com.example.demo.enums.AttendanceStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AttendanceCreateRequest {

    private UUID groupId;
    private List<Long> studentIds;
    private LocalDateTime attendanceTime;
    private AttendanceStatus status;

}
