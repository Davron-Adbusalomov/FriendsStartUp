package com.example.demo.management.controller;

import com.example.demo.management.model.UserEntity;
import com.example.demo.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class UsersController {

    private final UserRepository userRepository;

    @PreAuthorize("hasAnyAuthority('TOGGLE_LOGIN')")
    @PutMapping("/{userId}/toggle-login")
    public ResponseEntity<Void> toggleLogin(@PathVariable Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found"));
        user.setIsBlocked(!Boolean.TRUE.equals(user.getIsBlocked()));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

}
