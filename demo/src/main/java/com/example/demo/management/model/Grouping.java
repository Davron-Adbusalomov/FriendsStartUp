package com.example.demo.management.model;

import com.example.demo.test.model.Quiz;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "groups")
public class Grouping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String subject;

    private String time;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = "group_student",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "grouping")
    private List<Quiz> quizzes;

    public void assignStudent(Student student) {
        students.add(student);
    }

    public void assignTeacher(Teacher teacher) {
        this.teacher=teacher;
    }
}
