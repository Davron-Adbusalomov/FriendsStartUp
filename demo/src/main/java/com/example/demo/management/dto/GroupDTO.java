package com.example.demo.management.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private UUID subjectId;

    private String time;

    private Long teacherId;

    private String description;

    private Integer durationInMonths;

    private Date startDate;

    private TeacherSummaryDTO teacher;

    private List<StudentDTO> students = new ArrayList<>();

    private List<UUID> quizzes;

    private UUID centerId;
}
