package com.example.demo.management.repository;

import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.model.rbac.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(RolesEnum name);
}
