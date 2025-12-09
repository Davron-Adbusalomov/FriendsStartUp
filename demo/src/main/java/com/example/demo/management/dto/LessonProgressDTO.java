package com.example.demo.management.dto;

import com.example.demo.management.model.Lesson;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class LessonProgressDTO {

    private UUID id;

    private UUID lessonId;

    private Lesson lesson;

    private Long studentId;

    private UUID centerId;

    private Boolean completed = false;

    private Date completedAt;

}
