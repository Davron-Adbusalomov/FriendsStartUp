package com.example.demo.management.controller;

import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController  // Changed from @Controller to @RestController for automatic @ResponseBody
@RequestMapping("api/v1/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(summary = "Getting group list", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('GET_GROUPS_LIST')")
    @GetMapping("/getAllGroups")
    public ResponseEntity<?> getAll(Pageable pageable) {
        try {
            return ResponseEntity.ok(groupService.getGroups(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error occurred!");
        }
    }

    @Operation(summary = "Getting group by id", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('GET_GROUP')")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable UUID id) {
        try {
            GroupDTO grouping = groupService.getGroupById(id);
            return ResponseEntity.ok(grouping);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Register group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('CREATE_GROUP')")
    @PostMapping("/createGroup")
    public ResponseEntity<?> registerGroup(@RequestBody GroupDTO groupDTO) {
        try {
            GroupDTO savedGroup = groupService.registerGroup(groupDTO);
            return ResponseEntity.ok(savedGroup);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Deleting group", responses = {@ApiResponse(responseCode = "204", description = "No Content - Successfully deleted"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('DELETE_GROUP')")
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteGroupById(@PathVariable UUID id) {
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Update group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('UPDATE_GROUP')")
    @PutMapping("/updateGroup/{id}")
    public ResponseEntity<?> updateGroup(@RequestBody GroupDTO groupDTO, @PathVariable UUID id) {
        try {
            GroupDTO updatedGroup = groupService.updateGroup(groupDTO, id);
            return ResponseEntity.ok(updatedGroup);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Assign student to group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('ASSIGN_STUDENT_TO_GROUP')")
    @PostMapping("/assignStudentToGroup")
    public ResponseEntity<?> assignStudentToGroup(@RequestBody AssignUserToGroupDTO dto) {
        try {
            GroupDTO grouping = groupService.assignStudentToGroup(dto);
            return ResponseEntity.ok(grouping);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Deassign student from group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('DEASSIGN_STUDENT_FROM_GROUP')")
    @PostMapping("/deassignStudentFromGroup")
    public ResponseEntity<?> deassignStudentFromGroup(@RequestBody AssignUserToGroupDTO dto) {
        try {
            GroupDTO grouping = groupService.deassignStudentFromGroup(dto);
            return ResponseEntity.ok(grouping);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Assign teacher to group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('ASSIGN_TEACHER_TO_GROUP')")
    @PostMapping("/assignTeacherToGroup")
    public ResponseEntity<?> assignTeacherToGroup(@RequestBody AssignUserToGroupDTO dto) {
        try {
            GroupDTO grouping = groupService.assignTeacherToGroup(dto);
            return ResponseEntity.ok(grouping);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
