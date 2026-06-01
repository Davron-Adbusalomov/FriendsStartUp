package com.example.demo.management.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupDTO {

    private UUID id;

    private String name;

    private String image;

    @Schema(type = "string", format = "binary")
    private MultipartFile imageFile;

    private UUID subjectId;

    private String time;

    private Long teacherId;

    private String description;

    private Integer durationInMonths;

    private Date startDate;

    private Boolean isStudentAccessible = false;

    private Double progressPercentage;

    private TeacherSummaryDTO teacher;

    private List<StudentDTO> students = new ArrayList<>();

    private UUID courseId;

    private UUID centerId;
}
