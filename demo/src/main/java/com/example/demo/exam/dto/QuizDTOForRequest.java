package com.example.demo.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTOForRequest {
    private UUID id;

    private Long duration;

    private int questions_num;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    private UUID groupingId;

    private Long teacherId;

    private List<UUID> questions;

    public String title;
}
