package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpcomingTaskDTO {
    private String title;
    private String type;
    private String groupName;
    private LocalDateTime scheduledTime;
}
