package com.example.demo.management.repository;

import com.example.demo.management.model.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CenterRepository extends JpaRepository<Center, UUID> {
    Optional<Center> findBySubdomain(String subdomain);
}
