package com.example.demo.management.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LessonDetailsDTO {
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

    private String inspectorImage;

    private List<AttachmentDTO> resources;
}
