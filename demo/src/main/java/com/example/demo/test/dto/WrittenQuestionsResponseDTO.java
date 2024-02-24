package com.example.demo.test.dto;

import com.example.demo.test.model.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WrittenQuestionsResponseDTO {
    private Long id;

    private Long quizId;

    private Long mark;
}
