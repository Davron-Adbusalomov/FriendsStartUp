package com.example.demo.management.repository;

import com.example.demo.enums.PermissionEnum;
import com.example.demo.management.model.rbac.UserPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPermissionsRepository extends JpaRepository<UserPermissionEntity, String> {
    List<UserPermissionEntity> findByUserId(long userId);

    Optional<UserPermissionEntity> findByName(PermissionEnum name);

    void deleteByUserId(long userId);

    void deleteByUserIdAndName(Long userId, PermissionEnum name);
}
