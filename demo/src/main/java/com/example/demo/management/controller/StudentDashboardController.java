package com.example.demo.management.controller;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.management.dto.StudentDashboardDTO;
import com.example.demo.management.service.StudentDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/student/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_STUDENT_DASHBOARD')")
    @Operation(
            summary = "Get student dashboard",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
            })
    public ResponseEntity<StudentDashboardDTO> getDashboard(
            @RequestParam(required = false) String month,
            Locale locale) {
        return ResponseEntity.ok(
                dashboardService.getDashboard(CurrentUserUtils.getUserId(), month, locale));
    }
}
