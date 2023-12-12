package com.example.demo.test.model;

import com.example.demo.management.model.Grouping;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Test {

    @Id
    private Long id;

    private String name;

    private boolean taken;

    @ManyToOne
    private Grouping grouping;
}
