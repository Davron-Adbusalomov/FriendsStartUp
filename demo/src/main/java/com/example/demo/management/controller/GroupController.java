package com.example.demo.management.controller;

import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("/getAllGroups")
    public ResponseEntity<?> getAll(){
        return groupService.getGroups();
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

    @PutMapping("{groupId}/studentToGroup/{studentId}")
    public ResponseEntity<?> assignStudentToGroup(@PathVariable Long groupId, @PathVariable Long studentId){
        return groupService.assignStudentToGroup(studentId, groupId);
    }

    @PutMapping("{groupId}/teacherToGroup/{teacherId}")
    public ResponseEntity<?> assignTeacherToGroup(@PathVariable Long groupId, @PathVariable Long teacherId){
        return groupService.assignTeacherToGroup(teacherId, groupId);
    }

}
