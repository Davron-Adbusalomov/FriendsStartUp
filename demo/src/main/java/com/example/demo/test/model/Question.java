package com.example.demo.test.model;

import com.example.demo.management.model.Teacher;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Data
public class Question{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String type;

    private String right_answer;

    private int mark;

    // option

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToMany(mappedBy = "questions")
    private Set<Quiz> quizzes;

}
