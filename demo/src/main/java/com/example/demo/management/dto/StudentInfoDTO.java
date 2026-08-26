package com.example.demo.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StudentInfoDTO {

    private Long id;

    private String fullName;

    @Schema(type = "string", format = "binary") // 🔥 SHU QATOR
    private MultipartFile image;

    private String email;

    private String phoneNumber;

    private String password;

    private String parentContact;

    private String parentName;

    private String imageUrl;
}

