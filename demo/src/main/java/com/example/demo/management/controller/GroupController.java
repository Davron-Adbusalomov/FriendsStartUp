package com.example.demo.management.controller;

import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(
            summary = "Getting group list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('GET_GROUPS_LIST')")
    @GetMapping("/getAllGroups")
    public ResponseEntity<?> getAll(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(groupService.getGroups());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred!");
        }
    }

    @Operation(
            summary = "Getting group by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('GET_GROUP')")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id){
        return groupService.getGroupById(id);
    }

    @Operation(
            summary = "Register group by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('CREATE_GROUP')")
    @PostMapping("/createGroup")
    public ResponseEntity<?> registerGroup(@RequestBody GroupDTO groupDTO){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(groupService.registerGroup(groupDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Deleting group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('DELETE_GROUP')")
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteGroupById(@PathVariable Long id){
        return groupService.deleteGroup(id);
    }

    @Operation(
            summary = "Update group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('UPDATE_GROUP')")
    @PutMapping("/updateGroup/{id}")
    public ResponseEntity<?> updateGroup(@RequestBody GroupDTO groupDTO, @PathVariable Long id){
        return groupService.updateGroup(groupDTO, id);
    }

    @Operation(
            summary = "Assign student to group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('ASSIGN_STUDENT_TO_GROUP')")
    @PutMapping("/assignStudentToGroup")
    public ResponseEntity<?> assignStudentToGroup(@RequestBody AssignUserToGroupDTO assignUserToGroupDTO){
        return groupService.assignStudentToGroup(assignUserToGroupDTO);
    }

    @Operation(
            summary = "DeAssign student to group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('DEASSIGN_STUDENT_FROM_GROUP')")
    @PutMapping("/deassignStudentFromGroup")
    public ResponseEntity<?> deassignStudentToGroup(@RequestBody AssignUserToGroupDTO assignUserToGroupDTO){
        return groupService.deassignStudentFromGroup(assignUserToGroupDTO);
    }

    @Operation(
            summary = "Assign student to group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('ASSIGN_TEACHER_TO_GROUP')")
    @PutMapping("/assignTeacherToGroup")
    public ResponseEntity<?> assignTeacherToGroup(@RequestBody AssignUserToGroupDTO assignUserToGroupDTO){
        return groupService.assignTeacherToGroup(assignUserToGroupDTO);
    }

}
