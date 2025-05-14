package com.example.demo.management.repository;

import com.example.demo.management.model.rbac.DefaultPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultPermissionsRepository extends JpaRepository<DefaultPermissionEntity, Long> {

}
