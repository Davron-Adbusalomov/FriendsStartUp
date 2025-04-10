package com.example.demo.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Schema(name = "StudentDto")
public class StudentDTO {
    private Long id;

    @NotNull(message = "Name is required")
    @Schema(example = "John Doe")
    private String name;

    @NotNull(message = "Student number is required")
    @Schema(example = "12345")
    private Long number;

    @Schema(example = "john.doe@example.com")
    private String parentContact;

    @Schema(example = "chat123")
    private String parentChatId;

    // For example, a comma-separated list of group names.
    @Schema(example = "Group A, Group B")
    private List<String> groupNames;
}
