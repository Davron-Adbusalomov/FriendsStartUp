package com.example.demo.test.dto;

import com.example.demo.test.model.Question;
import com.example.demo.test.model.Quiz_Results;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

        private Set<Question> questions;

       // private Quiz_Results quizResult;
    }

