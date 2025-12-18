package com.example.demo.management.controller;

import com.example.demo.enums.EnrollmentStatus;
import com.example.demo.management.dto.EnrollmentDTO;
import com.example.demo.management.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @Operation(summary = "Getting group of enrollments", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('GET_ENROLLMENTS_LIST')")
    @GetMapping
    public Page<EnrollmentDTO> getAllEnrollments(
            @RequestParam(name = "status", required = false) EnrollmentStatus status,
            @RequestParam(name = "studentId", required = false) Long studentId,
            Pageable pageable
    ) {
        return enrollmentService.getEnrollmentsByStatus(status, studentId, pageable);
    }

    @Operation(summary = "process enrollment", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('PROCESS_ENROLLMENT')")
    @GetMapping("/enroll")
    public void processEnrollment(@RequestParam EnrollmentDTO enrollmentDTO) {
        enrollmentService.processEnrollment(enrollmentDTO);
    }
}
