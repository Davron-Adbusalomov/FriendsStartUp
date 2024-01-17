package com.example.demo.management.dto;

import com.example.demo.management.security.Role;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    private Long id;

    private String name;

    private String username;

    private String password;

    private LocalDate dateOfBirth;

    private Role role;
}
