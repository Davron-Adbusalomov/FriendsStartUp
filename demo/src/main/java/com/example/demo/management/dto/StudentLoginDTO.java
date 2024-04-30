package com.example.demo.management.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentLoginDTO {
    private StudentDTO user;

    private LocalDateTime loginTime;

    @JsonIgnore
    private String token;
}
