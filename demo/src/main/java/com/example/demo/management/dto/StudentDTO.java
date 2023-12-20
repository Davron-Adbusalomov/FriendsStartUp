package com.example.demo.management.dto;

import lombok.*;

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

    private String role;

    private String parent_email;

    private String parent_contact;

    private String relationship;

}
