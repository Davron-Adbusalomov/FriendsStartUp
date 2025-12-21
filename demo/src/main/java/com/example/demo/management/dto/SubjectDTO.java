package com.example.demo.management.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SubjectDTO {
    private UUID id;
    private String name;
    private String description;
    private String code;
    private UUID centerId;
}
