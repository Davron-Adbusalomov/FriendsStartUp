package com.example.demo.management.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
//@Schema(description = "User's login credentials")
@Validated
public class SignInReqDto {
    @NotNull(message = "{username.not_null}")
    @Size(min = 4, max = 100, message = "{username.size}")
    private String username;
    @NotNull(message = "{password.not_null}")
    @Size(min = 6, max = 20, message = "{password.size}")
    private String password;

    private String role;
}

