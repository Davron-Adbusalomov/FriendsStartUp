package com.example.demo.management.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class LessonCommentCreateRequest {
    private Long userId;
    private String content;
    private UUID lessonId;
}
