package com.example.demo.management.dto;


import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.test.model.Quiz;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupDTO {

    private Long id;

    private String name;

    private String subject;

  //  private Teacher teacherDTO;

  //  private List<Student> studentDTOList;

    private List<Quiz> quizzes;
}
