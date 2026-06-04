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

    @Operation(
            summary = "create badge",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('CREATE_BADGE')")
    @PostMapping
    public ResponseEntity<BadgeDTO> create(
            @RequestBody BadgeRequestDto dto
    ) {
        return ResponseEntity.ok(badgeService.create(dto));
    }

    @Operation(
            summary = "Update badge",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('UPDATE_BADGE')")
    @PutMapping("/{id}")
    public ResponseEntity<BadgeDTO> update(
            @PathVariable UUID id,
            @RequestBody BadgeDTO dto
    ) {
        return ResponseEntity.ok(badgeService.update(id, dto));
    }

    @Operation(
            summary = "Delete badge",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('DELETE_BADGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        badgeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get badge",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('GET_BADGE')")
    @PostMapping("/{id}/assign/{studentId}")
    public ResponseEntity<BadgeDTO> assign(
            @PathVariable UUID id,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(
                badgeService.assign(id, studentId)
        );
    }

    @Operation(
            summary = "Get student badges",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('GET_BADGE')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<BadgeDTO>> getStudentBadges(
            @PathVariable Long studentId,
            Locale locale
    ) {
        return ResponseEntity.ok(
                badgeService.getStudentBadges(studentId, locale)
        );
    }
}

