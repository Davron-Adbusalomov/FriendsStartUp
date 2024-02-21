package com.example.demo.test.dto;

import com.example.demo.test.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTOForRequest {
    private Long id;

    private Long duration;

    private int questions_num;

    private Long groupingId;

    private Long teacherId;

    private List<Long> questions;
}
