package com.example.demo.management.model;

import com.example.demo.test.model.Test_Results;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Student {
    @Id
    private Long id;

    private String name;

    private Long age;

    private Long number;

    private String email;

    private String username;

    private String password;

    private String role;

    @ManyToMany
    @JoinTable(
            name = "group_student",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Grouping> groupings;

    @ManyToMany(mappedBy = "students")
    private List<Teacher> teachers;

    @ManyToOne
    @JoinColumn(name="parent_id")
    private Parent parent;

    @OneToOne
    @JoinColumn(name = "marks")
    private Test_Results testResults;


}
