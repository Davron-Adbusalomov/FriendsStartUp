package com.example.demo.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WrittenQuestionsResponseDTO {
    private Long id;

    private Long quizId;

    private Long mark;
}
