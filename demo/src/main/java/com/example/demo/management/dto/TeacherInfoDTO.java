package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherInfoDTO {
    private Long id;

    private String fullName;

    private String experience;

    private String subject;

    private String image;

    private String phoneNumber;

    private String email;

    private String password;
}
