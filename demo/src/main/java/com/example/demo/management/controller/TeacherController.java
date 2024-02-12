package com.example.demo.management.controller;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/getTeachers")
    public ResponseEntity<?> getAll(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(teacherService.getTeachers());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return teacherService.getById(id);
    }

    @DeleteMapping("deleteTeacher/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id){
        return teacherService.deleteTeacher(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTeacher(@RequestBody TeacherDTO teacherDTO, @PathVariable Long id){
        try {
            return teacherService.updateTeacher(teacherDTO,id);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/loginTeacher")
    public ResponseEntity<?> loginTeacher(@RequestBody TeacherDTO teacherDTO){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(teacherService.loginTeacher(teacherDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
