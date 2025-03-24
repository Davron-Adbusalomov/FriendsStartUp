package com.example.demo.management.model;

import com.example.demo.exam.model.Quiz_Results;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Data
public class Student {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long number;

    private String parent_contact;

    private String parent_chatId;

    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private List<Grouping> groupings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<Quiz_Results> quizResults = new ArrayList<>();
}
