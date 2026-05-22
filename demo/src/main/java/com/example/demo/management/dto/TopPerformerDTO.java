package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopPerformerDTO {
    private String studentName;
    private String groupName;
    private double score;
    private long rank;
}
