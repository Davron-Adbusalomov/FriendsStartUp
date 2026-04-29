package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentTaskDTO {
    private String studentName;
    private String history;
    private String topic;
    private String taskName;
    private String submissionDate;
    private String status;
}
