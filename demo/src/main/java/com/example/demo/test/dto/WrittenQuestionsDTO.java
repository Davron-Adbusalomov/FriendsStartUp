package com.example.demo.test.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WrittenQuestionsDTO {
    private Long id;

    private Long quizId;

    private String studentAnswer;

    private String correct_answer;

    private Long score;

    private Long max_score;

    private String questionTitle;

    private Long studentId;
}
