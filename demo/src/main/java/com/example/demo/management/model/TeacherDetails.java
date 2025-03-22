package com.example.demo.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "teacher_details")
public class TeacherDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String experience;
    private String image;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user; // Link back to User entity
}
