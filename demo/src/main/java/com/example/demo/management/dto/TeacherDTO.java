package com.example.demo.management.dto;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import lombok.*;
import org.apache.catalina.Group;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherDTO {
    private Long id;

    private String name;

    private Long age;

    private String email;

    private Long phone_num;

    private String username;

    private String password;

}
