package com.example.demo.management.dto;


import com.example.demo.exam.dto.QuizDTO;
import com.example.demo.exam.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupDTO {

    private Long id;

    private String name;

    private String subject;

    private String time;

    private String teacherName;

    private Long teacherId;

    private List<StudentDTO> students = new ArrayList<>();

    private List<Long> quizzes;

    private LocalDateTime createdAt;
}
