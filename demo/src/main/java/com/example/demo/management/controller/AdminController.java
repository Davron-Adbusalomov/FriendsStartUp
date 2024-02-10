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


@CrossOrigin
@Controller
@RequestMapping("api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/createADMIN")
    public ResponseEntity<?> createAdmin(@RequestBody AdminDTO adminDTO){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(adminService.addAdmin(adminDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAdmins")
    public ResponseEntity<?> getAdmins(){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getAdmins());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> updateAdmin(@RequestBody AdminDTO adminDTO, @PathVariable Long id) throws Exception {
        try {
            return adminService.updateAdmin(adminDTO,id);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createSTUDENT")
    public ResponseEntity<?> registerUser(@RequestBody StudentDTO studentDTO){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.registerStudent(studentDTO));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createTEACHER")
    public ResponseEntity<?> registerTeacher(@RequestBody TeacherDTO teacherDTO){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.registerTeacher(teacherDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createGROUP")
    public ResponseEntity<?> registerGroup(@RequestBody GroupDTO groupDTO){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.registerGroup(groupDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AdminDTO adminDTO){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.loginAdmin(adminDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
