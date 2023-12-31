package com.example.demo.management.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parent {
    @Id
    private Long id;

    private String name;

    private Long phone_number;

    private String email;

    @OneToMany(mappedBy = "parent")
    private List<Student> students;
}
