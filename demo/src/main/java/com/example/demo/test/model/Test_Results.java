package com.example.demo.test.model;

import com.example.demo.management.model.Student;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Test_Results{

    @Id
    private Long id;

    @OneToOne(mappedBy = "testResults")
    private Student student;
}
