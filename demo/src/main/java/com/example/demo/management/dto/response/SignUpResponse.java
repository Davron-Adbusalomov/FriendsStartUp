package com.example.demo.management.dto.response;

import com.example.demo.management.dto.RoleDto;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
public class SignUpResponse {
    private Long userId;
    private String username;
    private Set<RoleDto> roles;
    private boolean passConfirmationCode = false;
}
