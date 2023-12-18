package com.example.demo.test.dto;

import com.example.demo.test.model.Question;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class QuizDTO {
    private Long id;

    private String title;

    private Long teacherId;

    private List<Question> questions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
