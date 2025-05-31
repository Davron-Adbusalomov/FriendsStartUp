package com.example.demo.management.dto;

import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.security.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherDTO {
    private Long id;

    private String name;

    private String image;

    private String experience;

    private String subject;

    private Long phone_num;

    private List<GroupDTO> groupList;

    private List<RolesEnum> roles;

    private LocalDateTime createdAt;

//    private String username;
//
//    private String password;
//
//    private Role role;

}
