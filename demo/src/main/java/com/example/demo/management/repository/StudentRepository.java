package com.example.demo.management.repository;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String aLong);

    @Query("SELECT g FROM Grouping g JOIN g.students s WHERE s.id = :groupId")
    List<Grouping> findByGroupId(@Param("groupId") Long groupId);


}
