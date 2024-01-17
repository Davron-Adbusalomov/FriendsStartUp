package com.example.demo.management.dto;

import com.example.demo.management.security.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherDTO {
    private Long id;

    private String name;

    private Long age;

    private String email;

    private LocalDate dateOfBirth;

    private Long phone_num;

    private String username;

    private String password;

    private Role role;

}
