package com.example.demo.management.model;

import com.example.demo.test.model.Question;
import com.example.demo.test.model.Quiz;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long age;

    private String email;

    private Long phone_num;

    private String username;

    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "teacher")
    private Set<Grouping> groupings= new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "teacher")
    private List<Quiz> quizzes;

    @OneToMany(mappedBy = "teacher")
    private List<Question> questions;



}
