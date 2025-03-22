package com.example.demo.management.authentication.dto;

import com.example.demo.management.model.rbac.RoleEntity;
//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

//@Schema(description = "Authentication details")
public class AuthenticationDetailsDto implements UserDetails {
    @Getter
    private final Long id;
    @Getter
    private final Long employeeId;
    private final String username;
    private final String password;
    private final Set<String> permissions;
    private final Set<RoleEntity> roles;

    public AuthenticationDetailsDto(Long id, Long employeeId, String username, String password, Set<RoleEntity> roles, Set<String> permissions) {
        this.id = id;
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.permissions = permissions;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        roles.forEach(
                role -> {
                    permissions.add(role.getName().name());
                    Set<String> defaultPermission = role.getDefaultPermissions().stream().map(dp -> dp.getName().name()).collect(Collectors.toSet());
                    permissions.forEach(permission -> {
                        if (!defaultPermission.contains(permission)) {
                            defaultPermission.remove(permission);
                        }
                    });
                    permissions.addAll(defaultPermission);
                });

        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
