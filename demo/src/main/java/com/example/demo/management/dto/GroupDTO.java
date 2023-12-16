package com.example.demo.management.dto;


import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.test.model.Test;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

    private Long id;

    private String name;

    private String subject;

    private Teacher teacherDTO;

    private List<Student> studentDTOList;

    private List<Test> tests;
}
