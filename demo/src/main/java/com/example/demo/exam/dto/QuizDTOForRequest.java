package com.example.demo.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTOForRequest {
    private UUID id;

    private Long duration;

    private int questions_num;

    private Long startTime;

    private UUID groupingId;

    private Long teacherId;

    private List<UUID> questions;
}
