package com.example.demo.exam.model;

import com.example.demo.management.model.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WrongAnswersAnalyze {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    private UUID question_id;

    private UUID quiz_id;

    private String wrong_answer;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

}
