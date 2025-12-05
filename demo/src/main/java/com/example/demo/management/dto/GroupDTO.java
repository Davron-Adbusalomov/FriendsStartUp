package com.example.demo.management.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupDTO {

    private UUID id;

    private String name;

    private String subject;

    private String time;

    private String teacherName;

    private Long teacherId;

    private List<StudentDTO> students = new ArrayList<>();

    private List<UUID> quizzes;

    private LocalDateTime createdAt;

    private UUID centerId;
}
