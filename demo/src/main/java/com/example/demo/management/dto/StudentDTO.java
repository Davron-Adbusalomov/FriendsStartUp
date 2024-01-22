package com.example.demo.management.dto;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.security.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentDTO {

    private Long id;

    private String name;

    private Long age;

    private Long number;

    private String email;

    private String username;

    private String password;

    private Role role;

    private LocalDate dateOfBirth;

    private String parent_email;

    private String parent_contact;

    private String groupName;

    private List<Grouping> groupList;

    private String parent_chatId;

    private String relationship;

    private String token;

}
