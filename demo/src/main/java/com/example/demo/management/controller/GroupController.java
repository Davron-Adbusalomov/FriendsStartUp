package com.example.demo.management.controller;

import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("/getAllGroups")
    public ResponseEntity<?> getAll(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(groupService.getGroups());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred!");
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id){
        return groupService.getGroupById(id);
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteGroupById(@PathVariable Long id){
        return groupService.deleteGroup(id);
    }

    @PutMapping("/updateGroup/{id}")
    public ResponseEntity<?> updateGroup(@RequestBody GroupDTO groupDTO, @PathVariable Long id){
        return groupService.updateGroup(groupDTO, id);
    }

    @PutMapping("/assignStudentToGroup")
    public ResponseEntity<?> assignStudentToGroup(@RequestBody AssignUserToGroupDTO assignUserToGroupDTO){
        return groupService.assignStudentToGroup(assignUserToGroupDTO);
    }

    @PutMapping("/assignTeacherToGroup")
    public ResponseEntity<?> assignTeacherToGroup(@RequestBody AssignUserToGroupDTO assignUserToGroupDTO){
        return groupService.assignTeacherToGroup(assignUserToGroupDTO);
    }

}
