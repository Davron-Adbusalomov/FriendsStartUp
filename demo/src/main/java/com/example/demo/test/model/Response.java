package com.example.demo.test.model;

import com.example.demo.management.model.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String answer;

    private Long question_id;
}