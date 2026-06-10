package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterResponseDTO {
    private UUID id;
    private String name;
    private String subdomain;
    private String logo;
}
