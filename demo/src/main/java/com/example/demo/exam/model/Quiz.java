package com.example.demo.exam.model;

import com.example.demo.management.model.BaseEntity;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Quiz extends BaseEntity {

    private int questions_num;

    private Long duration;

    private LocalDateTime startTime;

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
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz_Results> quizResult;

    public void assignQuestion(Question question) {
        questions.add(question);
    }
}
