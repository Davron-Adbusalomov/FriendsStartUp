package com.example.demo.exam.dto;

import com.example.demo.exam.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTO {

        private UUID id;

        private String title;

        private Long duration;

        private int questionsNum;

        private UUID groupingId;

        private Long teacherId;

        private LocalDateTime createdAt;

        private LocalDateTime startTime;

        private Set<QuestionDTO> questions;

        private UUID courseId;

       // private QuizResults quizResult;
    }

