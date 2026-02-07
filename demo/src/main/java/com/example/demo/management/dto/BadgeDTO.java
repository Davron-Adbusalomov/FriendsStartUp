package com.example.demo.management.dto;

import lombok.Data;

@Data
public class BadgeDTO {

    private String name;

    private String description;

    private Boolean active;

    private Long studentId;
}

