package com.example.demo.test.model;

import com.example.demo.management.model.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Data
public class Question{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String group_name;

    private String type;

    private String right_answer;

    private int mark;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties("questions")
    private Teacher teacher;

    @JsonIgnore
    @ManyToMany(mappedBy = "questions")
    private Set<Quiz> quizzes;

    @OneToMany(mappedBy = "question")
    private List<Option> options = new ArrayList<>();

    public void assignOption(Option option) {
        options.add(option);
    }
}
