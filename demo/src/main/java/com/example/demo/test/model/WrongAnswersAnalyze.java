package com.example.demo.test.model;

import com.example.demo.management.model.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WrongAnswersAnalyze {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long question_id;

    private Long quiz_id;

    private String wrong_answer;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

}
