package com.example.demo.management.model.rbac;

import com.example.demo.enums.PermissionEnum;
import com.example.demo.management.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;


@Getter
@Setter
@Entity
@Table(name = "user_permission")
@SequenceGenerator(name = "base_seq_gen", sequenceName = "user_permission_seq", allocationSize = 1)
public class UserPermissionEntity extends BaseEntity implements GrantedAuthority {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionEnum name;

    @Override
    public String getAuthority() {
        return name.name();
    }
}
