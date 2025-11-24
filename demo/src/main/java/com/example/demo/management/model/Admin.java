package com.example.demo.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE admin SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
public class Admin extends BaseEntity{
    @Id
    private Long id;

    private String fullName;

    private String username;

    private String password;

    private String image;
}
