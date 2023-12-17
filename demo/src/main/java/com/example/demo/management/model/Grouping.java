package com.example.demo.management.model;

import com.example.demo.test.model.Quiz;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "groups")
public class Grouping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToMany(mappedBy = "groupings")
    private List<Student> students;

    @OneToMany(mappedBy = "grouping")
    private List<Quiz> quizzes;
}
