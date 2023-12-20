package com.example.demo.management.controller;

import com.example.demo.management.dto.*;
import com.example.demo.management.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/createAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody AdminDTO adminDTO){
        return adminService.addAdmin(adminDTO);
    }

    @GetMapping("/getById")
    public ResponseEntity<?> getById(@RequestBody AdminDTO adminDTO){
        return adminService.getAdminById(adminDTO);
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        return adminService.deleteById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAdmin(@RequestBody AdminDTO adminDTO, @PathVariable Long id){
        return adminService.updateAdmin(adminDTO,id);
    }

    @PostMapping("/registerStudent")
    public ResponseEntity<?> registerUser(@RequestBody StudentDTO studentDTO){
        return adminService.registerStudent(studentDTO);
    }

    @PostMapping("/registerTeacher")
    public ResponseEntity<?> registerTeacher(@RequestBody TeacherDTO teacherDTO){
        return adminService.registerTeacher(teacherDTO);
    }

    @PostMapping("/registerGroup")
    public ResponseEntity<?> registerGroup(@RequestBody GroupDTO groupDTO){
        return adminService.registerGroup(groupDTO);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AdminDTO adminDTO){
        return adminService.loginAdmin(adminDTO);
    }

}
