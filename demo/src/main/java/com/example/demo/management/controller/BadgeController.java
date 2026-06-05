package com.example.demo.management.controller;

import com.example.demo.management.dto.BadgeDTO;
import com.example.demo.management.dto.request.BadgeRequestDto;
import com.example.demo.management.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @Operation(summary = "Get all badges for the center")
    @PreAuthorize("hasAuthority('GET_BADGE')")
    @GetMapping
    public ResponseEntity<List<BadgeDTO>> getAll(Locale locale) {
        return ResponseEntity.ok(badgeService.getAll(locale));
    }

    @Operation(summary = "Get badges assigned to a student")
    @PreAuthorize("hasAuthority('GET_BADGE')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<BadgeDTO>> getStudentBadges(
            @PathVariable Long studentId,
            Locale locale
    ) {
        return ResponseEntity.ok(badgeService.getStudentBadges(studentId, locale));
    }

    @Operation(summary = "Create badge")
    @PreAuthorize("hasAuthority('CREATE_BADGE')")
    @PostMapping
    public ResponseEntity<BadgeDTO> create(@RequestBody BadgeRequestDto dto) {
        return ResponseEntity.ok(badgeService.create(dto));
    }

    @Operation(summary = "Update badge")
    @PreAuthorize("hasAuthority('UPDATE_BADGE')")
    @PutMapping("/{id}")
    public ResponseEntity<BadgeDTO> update(
            @PathVariable UUID id,
            @RequestBody BadgeDTO dto
    ) {
        return ResponseEntity.ok(badgeService.update(id, dto));
    }

    @Operation(summary = "Delete badge")
    @PreAuthorize("hasAuthority('DELETE_BADGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        badgeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign badge to student")
    @PreAuthorize("hasAuthority('UPDATE_BADGE')")
    @PostMapping("/{id}/assign/{studentId}")
    public ResponseEntity<BadgeDTO> assign(
            @PathVariable UUID id,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(badgeService.assign(id, studentId));
    }

    @Operation(summary = "Unassign badge from student")
    @PreAuthorize("hasAuthority('UPDATE_BADGE')")
    @DeleteMapping("/{id}/unassign/{studentId}")
    public ResponseEntity<Void> unassign(
            @PathVariable UUID id,
            @PathVariable Long studentId
    ) {
        badgeService.unassign(id, studentId);
        return ResponseEntity.noContent().build();
    }
}
