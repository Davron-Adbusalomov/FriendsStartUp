package com.example.demo.management.repository;

import com.example.demo.management.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID>, JpaSpecificationExecutor<Subject> {

}
