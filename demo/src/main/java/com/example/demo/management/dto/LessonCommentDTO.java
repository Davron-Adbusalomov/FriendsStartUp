package com.example.demo.management.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LessonCommentDTO {
    private UUID id;
    private String content;
    private UUID lessonId;
    private Long userId;
    private String author;
}
