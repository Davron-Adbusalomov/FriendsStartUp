package com.example.demo.management.repository;

import com.example.demo.management.model.Grouping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Grouping, Long> {
    Optional<Grouping> findByName(String aLong);

    List<Grouping> findByTeacherId(Long teacherId);

    @Query("SELECT g FROM Grouping g JOIN g.students s WHERE s.id = :studentId")
    List<Grouping> findByStudentId(@Param("studentId") Long studentId);
}
