package com.example.demo.management.dto.response;

import com.example.demo.management.dto.GroupDTO;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserLoginResponseDto {
    private Long id;

    private String username;

    private String fullName;

    private List<GroupDTO> groups;

    private Set<String> roles;

    private String image;
}
