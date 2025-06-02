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
public class Teacher extends BaseEntity {

    private String fullName;

    private String subject;

    private String experience;

    private String image;

    private String phoneNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "teacher")
    private List<Grouping> groupList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "teacher")
    private List<Quiz> quizzes;

    @OneToMany(mappedBy = "teacher")
    private List<Question> questions;
}
