package com.example.demo.exam.dto;

import lombok.Data;

@Data
public class StudentRankingsDTO {
    private String studentImage;
    private String studentName;
    private Double totalScore;
    private Integer rank;
}
