package com.example.demo.management.dto;

import com.example.demo.management.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentDTO {
    private Long id;

    private String name;

    private Long phone_number;

    private String email;

    private List<Student> studentDTOS;

}
