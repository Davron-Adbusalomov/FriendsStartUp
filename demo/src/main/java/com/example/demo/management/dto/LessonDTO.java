package com.example.demo.management.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class LessonDTO {
    private UUID id;

    private String title;

    private String description;

    private String videoUrl;

    private Integer duration;

    private Integer orderIndex;

    private UUID groupId;

    private UUID centerId;

    private String inspectorName;

    private String inspectorInfo;
}
