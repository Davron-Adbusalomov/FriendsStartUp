package com.example.demo.management.controller;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.management.dto.AdminDashboardDTO;
import com.example.demo.management.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_ADMIN_DASHBOARD')")
    @Operation(
            summary = "Get admin dashboard",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
            })
    public ResponseEntity<AdminDashboardDTO> getDashboard(
            @RequestParam(required = false) String month) {
        return ResponseEntity.ok(
                dashboardService.getDashboard(CurrentUserUtils.getCenterId(), month));
    }
}
