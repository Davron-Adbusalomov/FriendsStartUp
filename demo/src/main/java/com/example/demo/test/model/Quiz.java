package com.example.demo.test.model;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int questions_num;

    private Long duration;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "grouping_id")
    private Grouping grouping;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties("quizzes")
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = "quiz_question",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private Set<Question> questions = new HashSet<>();

    @OneToOne(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Quiz_Results quizResult;

    public void assignQuestion(Question question) {
        questions.add(question);
    }
}
