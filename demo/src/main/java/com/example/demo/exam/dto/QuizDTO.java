package com.example.demo.exam.dto;

import com.example.demo.exam.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTO {

        private Long id;

        private Long duration;

        private int questions_num;

        private Long groupingId;

        private Long teacherId;

        private LocalDateTime startTime;

        private List<Question> questions;

       // private Quiz_Results quizResult;
    }

