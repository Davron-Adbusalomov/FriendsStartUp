package com.example.demo.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "course")
@SQLDelete(sql = "UPDATE course SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String name;

    private String description;

    private String image;

    private Integer durationInMonths;

    @Column(name = "subject_id")
    private UUID subjectId;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;
}
