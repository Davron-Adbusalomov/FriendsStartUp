package com.example.demo.management.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SaveUserPermissionsDto {
    private Long userId;

    private List<String> permissions;
}
