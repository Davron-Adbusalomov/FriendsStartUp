package com.example.demo.management.repository;

import com.example.demo.management.model.Grouping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Grouping, Long> {
    Optional<Grouping> findByName(String aLong);

}
