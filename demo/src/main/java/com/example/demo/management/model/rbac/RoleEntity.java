package com.example.demo.management.model.rbac;

import com.example.demo.management.authentication.enums.RolesEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@SequenceGenerator(name = "base_seq_gen", sequenceName = "role_seq", allocationSize = 1)
public class RoleEntity implements GrantedAuthority {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RolesEnum name;

    @Column(name = "privilege", nullable = false)
    private Integer privilege;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_default_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "default_permission_name"))
    private Set<DefaultPermissionEntity> defaultPermissions;


    @Override
    public String getAuthority() {
        return name.name();
    }
}
