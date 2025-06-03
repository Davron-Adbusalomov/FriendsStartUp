package com.example.demo.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends BaseEntity{
    @Id
    private Long id;

    private String fullName;

    private String username;

    private String password;

    private String image;
}
