package com.example.demo.management.repository;

import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.model.UserEntity;
import com.example.demo.management.model.rbac.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("SELECT u.roles FROM UserEntity u WHERE u.id = :userId")
    Set<RoleEntity> findRolesByUserId(@Param("userId") Long userId);
}
