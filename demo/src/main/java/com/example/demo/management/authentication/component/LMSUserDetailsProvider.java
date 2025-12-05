package com.example.demo.management.authentication.component;

import com.example.demo.management.authentication.dto.AuthenticationDetailsDto;
import com.example.demo.management.model.rbac.RoleEntity;
import com.example.demo.management.repository.UserPermissionsRepository;
import com.example.demo.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LMSUserDetailsProvider implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserPermissionsRepository userPermissionsRepository;
//    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = userRepository.findByUsername(username);
        var employeeRef = new Object() {
            final Long employeeId = null;
        };
        UUID centerId;
        Set<RoleEntity> roles;
        Set<String> permissions;
        if (userOptional.isPresent()) {
            centerId = userOptional.get().getCenterId();
            var userPermissions = userPermissionsRepository.findByUserId(userOptional.get().getId());
//            var employee = employeeRepository.findByUserId(userOptional.get().getId());
//            employee.ifPresent(employeeEntity -> employeeRef.employeeId = employeeEntity.getId());
            roles = userOptional.get().getRoles();
            if (!userPermissions.isEmpty()) {
                permissions = userPermissions.stream().map(p -> p.getName().name()).collect(Collectors.toSet());
            } else {
                permissions = new HashSet<>();
            }
        } else {
            throw new UsernameNotFoundException(
                    String.format("User not exists by Username or Email : [%s]", username)
            );
        }


        return userOptional.map(
                        user -> new AuthenticationDetailsDto(user.getId(), employeeRef.employeeId, user.getUsername(), user.getPassword(), roles, permissions, centerId))
                .get();
    }
}
