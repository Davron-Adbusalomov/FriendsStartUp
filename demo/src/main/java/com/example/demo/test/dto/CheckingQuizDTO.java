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
public class CheckingQuizDTO {
    private Long student_id;

    private List<Response> wrong_answers;

    private List<Response> written_questions;

    private Long current_mark;
}
