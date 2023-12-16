package com.example.demo.management.repository;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String aLong);

}
