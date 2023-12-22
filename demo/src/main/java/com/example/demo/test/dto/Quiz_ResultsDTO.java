package com.example.demo.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quiz_ResultsDTO {
    private Long id;

    private Long mark;

    private Long quiz_id;

    private Long student_id;
}
