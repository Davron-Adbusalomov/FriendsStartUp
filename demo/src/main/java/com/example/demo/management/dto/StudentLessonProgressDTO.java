package com.example.demo.management.dto;

import com.example.demo.enums.LessonProgressEnum;
import lombok.Data;

import java.util.UUID;

@Data
public class StudentLessonProgressDTO {
    private UUID id;

    private String title;

    private String description;

    private String videoUrl;

    private Integer duration;

    private Integer orderIndex;

    private UUID groupId;

    private UUID centerId;

    private LessonProgressEnum lockingStatus = LessonProgressEnum.LOCKED;
}
