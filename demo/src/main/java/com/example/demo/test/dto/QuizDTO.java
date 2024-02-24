package com.example.demo.test.dto;

import com.example.demo.test.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

