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

    private String name;

    private int experience;

    private String subject;

    private String image;
}
