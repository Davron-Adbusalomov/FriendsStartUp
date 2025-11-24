package com.example.demo.management.model;

import com.example.demo.exam.model.Question;
import com.example.demo.exam.model.Quiz;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE teacher SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
public class Teacher extends BaseEntity {
    @Id
    private Long id;

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
