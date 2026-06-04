package com.example.demo.management.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class BadgeDTO {

    private UUID id;

    private String name;

    private String description;

    private Boolean active;

    private Long studentId;
}

