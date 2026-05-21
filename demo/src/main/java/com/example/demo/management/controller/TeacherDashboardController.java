package com.example.demo.management.controller;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.management.dto.TeacherDashboardDTO;
import com.example.demo.management.service.TeacherDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api/v1/teacher/dashboard")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService dashboardService;

    @GetMapping()
    @PreAuthorize("hasAuthority('GET_TEACHER_DASHBOARD')")
    @Operation(
            summary = "Getting teacher dashboard",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    public ResponseEntity<TeacherDashboardDTO> getDashboard(
            @RequestParam(required = false) String groupId) {
        return ResponseEntity.ok(dashboardService.getDashboard(CurrentUserUtils.getUserId(), groupId));
    }
}
