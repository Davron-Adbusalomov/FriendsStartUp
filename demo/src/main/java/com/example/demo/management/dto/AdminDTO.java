package com.example.demo.management.dto;

import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.security.Role;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    private Long id;

    private String fullName;

    private String username;

    private String password;

    private String image;

    private LocalDateTime createdAt;

    private List<RolesEnum> rolesEnums = new ArrayList<>();
}
