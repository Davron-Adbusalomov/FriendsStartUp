package com.example.demo.management.dto;

import lombok.Data;

@Data
public class AdminInfoDTO {
    private Long id;

    private String fullName;

    private String image;

    private String email;

    private String phoneNumber;

    private String password;
}
