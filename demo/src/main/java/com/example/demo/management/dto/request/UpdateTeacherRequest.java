package com.example.demo.management.dto.request;

import com.example.demo.management.dto.SubjectDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTeacherRequest {
    private Long id;

    private String fullName;

    private String experience;

    private UUID subjectId;

    @Schema(type = "string", format = "binary")
    private MultipartFile image;

    private String phoneNumber;

    private String email;

    private String password;

    private SubjectDTO subject;
}
