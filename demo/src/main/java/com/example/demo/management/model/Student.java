package com.example.demo.management.model;

import com.example.demo.exam.model.Quiz_Results;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.*;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SQLDelete(sql = "UPDATE student SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class Student extends BaseEntity{
    @Id
    private Long id;

    private String fullName;

    private String phoneNumber;

    private String email;

    private String image;

    private String parentContact;

    private String parentChatId;

    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private List<Grouping> groupings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<Quiz_Results> quizResults = new ArrayList<>();

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;
}
