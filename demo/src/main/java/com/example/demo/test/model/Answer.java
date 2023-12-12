package com.example.demo.test.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Answer{

    @Id
    private Long id;

    private String answer;

    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "test_question_id")
    private Question question;

}
