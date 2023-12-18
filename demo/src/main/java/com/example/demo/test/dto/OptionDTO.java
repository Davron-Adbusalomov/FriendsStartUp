package com.example.demo.test.dto;

import lombok.Data;

@Data
public class OptionDTO {

    private Long id;
    private String text;
    private Long questionId; // Use the questionId property to represent the associated question's ID
    private Boolean isCorrect;

}
