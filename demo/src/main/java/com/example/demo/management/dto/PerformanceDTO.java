package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDTO {
    private String day;
    private int grade10;
    private int grade11;
    private int grade12;
}
