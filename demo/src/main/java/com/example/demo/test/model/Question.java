package com.example.demo.test.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;


@Entity
@Data
public class Question{

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    private List<Answer> answers;

    @Id
    private Long question_id;

    private String question;

}
