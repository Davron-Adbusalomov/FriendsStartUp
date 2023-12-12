package com.example.demo.management.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Teacher {
    @Id
    private Long id;

    private String name;

    private Long age;

    private String email;

    private Long phone_num;

    private String username;

    private String password;

    @OneToMany(mappedBy = "teacher")
    private List<Grouping> groupings;

    @ManyToMany
    @JoinTable(
    name = "teacher_student",
    joinColumns = @JoinColumn(name = "teacher_id"),
    inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students;

}
