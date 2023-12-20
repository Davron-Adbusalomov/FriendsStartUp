package com.example.demo.management.model;

import com.example.demo.test.model.Quiz_Results;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long age;

    private Long number;

    private String email;

    private String username;

    private String password;

    private String role;

    private String parent_email;

    private String parent_contact;

    private String relationship;

    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private Set<Grouping> groupings = new HashSet<>();

    @OneToMany(mappedBy = "student")
    private List<Quiz_Results> testResults;

}
