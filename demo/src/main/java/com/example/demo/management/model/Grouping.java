package com.example.demo.management.model;

import com.example.demo.exam.model.Quiz;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "groups")
@SQLDelete(sql = "UPDATE groups SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
public class Grouping extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

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

    public void deassignStudent(Student student) {
        students.remove(student);
    }

    public void assignTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}