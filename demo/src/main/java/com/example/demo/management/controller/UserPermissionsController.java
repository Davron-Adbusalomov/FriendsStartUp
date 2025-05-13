package com.example.demo.management.controller;

import com.example.demo.management.dto.request.SaveUserPermissionsDto;
import com.example.demo.management.service.UserPermissionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user_permissions")
public class UserPermissionsController {

    private final UserPermissionsService userPermissionsService;

    @Operation(
            summary = "Get permissions by user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - User not found"),
            }
    )
    @PreAuthorize("hasAuthority('GET_PERMISSIONS_BY_USER_ID')")
    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<List<String>> getPermissions(@PathVariable Long userId) {
        List<String> permissions = userPermissionsService.findByUserId(userId);
        return ResponseEntity.ok(permissions);
    }

    @Operation(
            summary = "Save permissions by user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - User not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('SAVE_USER_PERMISSIONS')")
    @PostMapping("/save")
    public ResponseEntity<?> savePermissions(@RequestBody SaveUserPermissionsDto dto) {
        try {
            userPermissionsService.save(dto);
            return ResponseEntity.status(HttpStatus.OK).body("Saved");
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
    }


}
