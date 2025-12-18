package com.example.demo.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@ToString
public class SignUpRequest {
    @NotNull(message = "username is required")
    private String username;
    private String fullName;
    private Set<String> roles;
    private List<UUID> groups;
    private boolean isSendConfirmationCode = false;
}
