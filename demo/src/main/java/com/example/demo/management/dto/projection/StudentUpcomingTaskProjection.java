package com.example.demo.management.dto.projection;

import java.time.LocalDateTime;

public interface StudentUpcomingTaskProjection {
    String getTitle();
    String getType();
    String getGroupName();
    LocalDateTime getScheduledTime();
}
