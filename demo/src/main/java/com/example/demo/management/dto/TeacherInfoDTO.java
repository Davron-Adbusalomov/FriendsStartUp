package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherInfoDTO {
    private Long id;

    private String fullName;

    private String experience;

    private UUID subjectId;

    private String image;

    private String phoneNumber;

    private String email;

    private String password;

    private SubjectDTO subject;
}
