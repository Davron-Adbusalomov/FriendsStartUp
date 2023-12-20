package com.example.demo.test.dto;

import com.example.demo.management.model.Teacher;
import com.example.demo.test.model.Option;
import com.example.demo.test.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;

    private String title;

    private String type;

    private String right_answer;

    private int mark;

    private Teacher teacher;

    private List<String> options;
}
