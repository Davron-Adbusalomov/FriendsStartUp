package com.example.demo.management.model.rbac;

import com.example.demo.enums.PermissionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;


@Getter
@Setter
@Entity
//@Table(name = EPharmConstant.Tables.USER_PERMISSION)
@SequenceGenerator(name = "base_seq_gen", sequenceName = "user_permission_seq", allocationSize = 1)
public class UserPermissionEntity implements GrantedAuthority {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionEnum name;

    @Override
    public String getAuthority() {
        return name.name();
    }
}
