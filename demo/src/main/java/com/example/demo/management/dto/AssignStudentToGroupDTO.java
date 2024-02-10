package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class AssignStudentToGroupDTO {
    private String groupName;

    private String studentUsername;
}
