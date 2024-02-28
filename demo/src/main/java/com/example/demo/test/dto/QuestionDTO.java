package com.example.demo.test.dto;

import com.example.demo.management.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;

    private String title;

    private String level;

    private String type;

    private String right_answer;

    private int mark;

    private Long teacherId;

    private List<String> options;
}
