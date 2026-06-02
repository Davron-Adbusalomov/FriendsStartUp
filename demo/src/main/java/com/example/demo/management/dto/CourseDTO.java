package com.example.demo.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class CourseDTO {
    private UUID id;
    private String name;
    private String description;
    private String image;
    @Schema(type = "string", format = "binary")
    private MultipartFile imageFile;
    private Integer durationInMonths;
    private UUID subjectId;
    private UUID centerId;
    private Long teacherId;
    private String teacherName;
}
