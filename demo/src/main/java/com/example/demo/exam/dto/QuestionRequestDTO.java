package com.example.demo.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDTO {
    private UUID id;

    private String title;

    private String level;

    private String type;

    private UUID subjectId;

    private String subjectName;

    @Schema(type = "string", format = "binary")
    private MultipartFile image;

    private String right_answer;

    private Integer mark;

    private Long teacherId;

    private String teacherName;

    private String topic;

    private List<String> options;
}

