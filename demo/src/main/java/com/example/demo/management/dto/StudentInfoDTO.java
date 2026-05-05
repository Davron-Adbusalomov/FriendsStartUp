package com.example.demo.management.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StudentInfoDTO {

    private Long id;

    private String fullName;

    private MultipartFile image;

    private String email;

    private String phoneNumber;

    private String password;

    private String parentContact;

    private String imageUrl;
}
