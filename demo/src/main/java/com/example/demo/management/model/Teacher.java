package com.example.demo.management.model;

import com.example.demo.exam.model.Question;
import com.example.demo.exam.model.Quiz;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    private Long id;

    private String name;

    private String subject;

    private String experience;

    private String image;

    private Long phone_num;

    @JsonIgnore
    @OneToMany(mappedBy = "teacher")
    private List<Grouping> groupList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "teacher")
    private List<Quiz> quizzes;

    @OneToMany(mappedBy = "teacher")
    private List<Question> questions;
}
