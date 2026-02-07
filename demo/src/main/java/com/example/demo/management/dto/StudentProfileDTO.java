package com.example.demo.management.dto;


import lombok.Data;

import java.util.List;

@Data
public class StudentProfileDTO {
    private Long id;

    private String fullName;

    private List<String> groupNames;

    private String image;

    private List<BadgeDTO> badges;

    private Integer coursesCompleted;

    private String averageGrade;

    private String status;
}
