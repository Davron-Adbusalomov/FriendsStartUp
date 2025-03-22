package com.example.demo.management.model.rbac;

import com.example.demo.enums.PermissionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Entity
//@Table(name = EPharmConstant.Tables.DEFAULT_PERMISSION)
@ToString
@SequenceGenerator(name = "base_seq_gen", sequenceName = "default_permission_seq", allocationSize = 1)
public class DefaultPermissionEntity implements GrantedAuthority {

    @Id
    @Column(name = "name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private PermissionEnum name;

    @Column(name = "description")
    private String description;

    @Override
    public String getAuthority() {
        return name.name();
    }
}
