package com.example.demo.management.controller;

import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("api/v1/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(summary = "Getting group list", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('GET_GROUPS_LIST')")
    @GetMapping("/getAllGroups")
    public Page<GroupDTO> getAll(
            @RequestParam(name = "studentId" ,required = false) Long studentId,
            @RequestParam(name = "teacherId" ,required = false) Long teacherId,
            @RequestParam(name = "name" ,required = false) String name,
            Pageable pageable) {
        return groupService.getGroups(pageable, teacherId, studentId, name);
    }

    @Operation(summary = "Getting group by id", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('GET_GROUP')")
    @GetMapping("/getById/{id}")
    public GroupDTO getGroupById(@PathVariable UUID id) {
        return groupService.getGroupById(id);
    }

    @Operation(summary = "Register group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('CREATE_GROUP')")
    @PostMapping("/createGroup")
    public GroupDTO registerGroup(@RequestBody GroupDTO groupDTO) {
        return groupService.registerGroup(groupDTO);
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
    public GroupDTO updateGroup(@RequestBody GroupDTO groupDTO, @PathVariable UUID id) {
        return groupService.updateGroup(groupDTO, id);
    }

    @Operation(summary = "Assign student to group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('ASSIGN_STUDENT_TO_GROUP')")
    @PostMapping("/assignStudentToGroup")
    public GroupDTO assignStudentToGroup(@RequestBody AssignUserToGroupDTO dto) {
        return groupService.assignStudentToGroup(dto);
    }

    @Operation(summary = "Deassign student from group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('DEASSIGN_STUDENT_FROM_GROUP')")
    @PostMapping("/deassignStudentFromGroup")
    public GroupDTO deassignStudentFromGroup(@RequestBody AssignUserToGroupDTO dto) {
        return groupService.deassignStudentFromGroup(dto);
    }

    @Operation(summary = "Assign teacher to group", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"), @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"), @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"), @ApiResponse(responseCode = "404", description = "Not found - Department not found"),})
    @PreAuthorize("hasAnyAuthority('ASSIGN_TEACHER_TO_GROUP')")
    @PostMapping("/assignTeacherToGroup")
    public GroupDTO assignTeacherToGroup(@RequestBody AssignUserToGroupDTO dto) {
        return groupService.assignTeacherToGroup(dto);
    }
}
