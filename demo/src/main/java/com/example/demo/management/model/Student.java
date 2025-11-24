package com.example.demo.management.model;

import com.example.demo.exam.model.Quiz_Results;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SQLDelete(sql = "UPDATE student SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
public class Student extends BaseEntity{
    @Id
    private Long id;

    private String fullName;

    private String phoneNumber;

    private String parent_contact;

    private String parent_chatId;

    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private List<Grouping> groupings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<Quiz_Results> quizResults = new ArrayList<>();
}
