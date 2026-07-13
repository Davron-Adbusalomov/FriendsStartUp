package com.example.demo.management.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class LessonDTO {
    private UUID id;
    private String title;
    private String description;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
    private String inspectorName;
    private String inspectorInfo;
    private UUID courseId;
    private UUID centerId;
    private MultipartFile video;
}
