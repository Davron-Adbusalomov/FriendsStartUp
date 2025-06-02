package com.example.demo.management.dto;

import com.example.demo.management.authentication.enums.RolesEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Schema(name = "StudentDto")
public class StudentDTO {
    private Long id;

    @NotNull(message = "Name is required")
    @Schema(example = "John Doe")
    private String fullName;

    @NotNull(message = "Student username is required")
    private String phoneNumber;

    @Schema(example = "john.doe@example.com")
    private String parentContact;

    @Schema(example = "chat123")
    private String parentChatId;

    @Schema(example = "Group A, Group B")
    private List<String> groupNames;

    private List<RolesEnum> roles;

    private LocalDateTime createdAt;

    private String status;
}
