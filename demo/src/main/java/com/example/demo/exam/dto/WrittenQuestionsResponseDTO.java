package com.example.demo.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WrittenQuestionsResponseDTO {
    private UUID id;

    private UUID quizId;

    private Long mark;
}
