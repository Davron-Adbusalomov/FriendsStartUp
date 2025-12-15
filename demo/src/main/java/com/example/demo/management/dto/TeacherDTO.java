package com.example.demo.management.dto;

import com.example.demo.management.authentication.enums.RolesEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherDTO {
    private Long id;

    private String fullName;

    private String image;

    private String experience;

    private String subject;

    private String phoneNumber;

    private String email;

    private List<GroupDTO> groupList;

    private List<RolesEnum> roles;

}
