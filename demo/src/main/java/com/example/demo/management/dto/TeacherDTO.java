package com.example.demo.management.dto;

import com.example.demo.management.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.Group;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO {
    private Long id;

    private String name;

    private Long age;

    private String email;

    private Long phone_num;

    private String username;

    private String password;

    private List<Group> groupDTOS;

    private List<Student> studentDTOList;

}
