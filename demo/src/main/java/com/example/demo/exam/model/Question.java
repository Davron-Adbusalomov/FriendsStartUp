package com.example.demo.exam.model;

import com.example.demo.management.model.BaseEntity;
import com.example.demo.management.model.Center;
import com.example.demo.management.model.Subject;
import com.example.demo.management.model.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.Data;
import org.hibernate.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Entity
@Data
@SQLDelete(sql = "UPDATE question SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class Question extends BaseEntity{
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String title;

    private String level;

    public String image;

    private String type;

    private String right_answer;

    private int mark;

    @Column(name = "subject_id")
    private UUID subjectId;

    @ManyToOne
    @JoinColumn(name = "subject_id", insertable = false, updatable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties("questions")
    private Teacher teacher;

    @JsonIgnore
    @ManyToMany(mappedBy = "questions")
    private Set<Quiz> quizzes;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Option> options = new ArrayList<>();

    public void assignOption(Option option) {
        options.add(option);
    }

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;
}
