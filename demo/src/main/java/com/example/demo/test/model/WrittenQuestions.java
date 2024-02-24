package com.example.demo.test.model;

import com.example.demo.management.model.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WrittenQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    private Long quizId;

    private String studentAnswer;

    private Long max_score;

    private String questionTitle;

    private String correct_answer;

    private Long score;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

}
