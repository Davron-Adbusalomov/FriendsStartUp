package com.example.demo.exam.model;


import lombok.Data;

import java.util.UUID;


@Data
public class Response {
    private String answer;

    private UUID questionId;
}
