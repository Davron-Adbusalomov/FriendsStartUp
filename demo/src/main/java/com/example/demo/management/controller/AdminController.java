package com.example.demo.management.controller;

import com.example.demo.management.dto.*;
import com.example.demo.management.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/admin")
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/createAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody AdminDTO adminDTO){
        return adminService.addAdmin(adminDTO);
    }

    @GetMapping("/getById/{adminID}")
    public ResponseEntity<?> getById(@PathVariable Long adminID){
        return adminService.getAdminById(adminID);
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        return adminService.deleteById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAdmin(@RequestBody AdminDTO adminDTO, @PathVariable Long id){
        return adminService.updateAdmin(adminDTO,id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registerStudent")
    public ResponseEntity<?> registerUser(@RequestBody StudentDTO studentDTO){
        return adminService.registerStudent(studentDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registerTeacher")
    public ResponseEntity<?> registerTeacher(@RequestBody TeacherDTO teacherDTO){
        return adminService.registerTeacher(teacherDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registerGroup")
    public ResponseEntity<?> registerGroup(@RequestBody GroupDTO groupDTO){
        return adminService.registerGroup(groupDTO);
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody AdminDTO adminDTO){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.loginAdmin(adminDTO));
    }

}
