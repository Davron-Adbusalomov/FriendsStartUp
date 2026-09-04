package com.example.demo.management.controller;

import com.example.demo.enums.AttendanceStatus;
import com.example.demo.management.dto.AttendanceDto;
import com.example.demo.management.dto.AttendanceMatrixDto;
import com.example.demo.management.dto.request.AttendanceCreateRequest;
import com.example.demo.management.model.Attendance;
import com.example.demo.management.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;


    @Operation(
            summary = "Create attendance for whole group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('CREATE_ATTENDANCE')")
    @PostMapping("/create")
    public List<AttendanceDto> createForGroup(@RequestBody AttendanceCreateRequest request) {
        return attendanceService.create(request);
    }


    @Operation(
            summary = "Get attendance list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Attendance list not found")
            }
    )
    @PreAuthorize("hasAuthority('GET_ATTENDANCE_LIST')")
    @GetMapping
    public Page<AttendanceDto> getAll(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime to,
            @RequestParam(required = false) Long studentId,
            Pageable pageable
    ) {
        return attendanceService.getAll(fullName, groupName, from, to, studentId, pageable);
    }

    @Operation(
            summary = "Get attendance matrix (group optional — omit to see all groups; use teacherId to scope to a teacher's own groups)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Attendance list not found")
            }
    )
    @PreAuthorize("hasAuthority('GET_ATTENDANCE_LIST')")
    @GetMapping("/matrix")
    public List<AttendanceMatrixDto> getAllOptimized(
            @RequestParam(required = false) UUID groupId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @RequestParam(required = false) Long studentId,
            Pageable pageable
    ) {
        return attendanceService.getAllOptimized(groupId, teacherId, from, to, pageable);
    }


    @Operation(
            summary = "Get attendance by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Attendance not found")
            }
    )
    @PreAuthorize("hasAuthority('GET_ATTENDANCE')")
    @GetMapping("/{id}")
    public AttendanceDto getById(@PathVariable UUID id) {
        return attendanceService.getById(id);
    }


    @Operation(
            summary = "Get attendance status for today by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Attendance not found")
            }
    )
    @PreAuthorize("hasAuthority('GET_ATTENDANCE')")
    @GetMapping("/today-status/{studentId}")
    public AttendanceStatus getTodayAttendanceStatus(@PathVariable Long studentId) {
        return attendanceService.getTodayAttendanceStatus(studentId);
    }

    @Operation(
            summary = "Update attendance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Attendance not found")
            }
    )
    @PreAuthorize("hasAuthority('UPDATE_ATTENDANCE')")
    @PutMapping("update/{id}")
    public AttendanceDto update(
            @PathVariable UUID id,
            @RequestBody AttendanceDto updated
    ) {
        return attendanceService.update(id, updated);
    }


    @Operation(
            summary = "Delete attendance (soft delete)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Attendance not found")
            }
    )
    @PreAuthorize("hasAuthority('DELETE_ATTENDANCE')")
    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable UUID id) {
        attendanceService.delete(id);
    }


    @Operation(
            summary = "Restore attendance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully restored"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Attendance not found")
            }
    )
    @PreAuthorize("hasAuthority('RESTORE_ATTENDANCE')")
    @PutMapping("/{id}/restore")
    public Attendance restore(@PathVariable UUID id) {
        return attendanceService.restore(id);
    }
}
