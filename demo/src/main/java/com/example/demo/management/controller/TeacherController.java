package com.example.demo.management.controller;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/getTeachers")
    public ResponseEntity<?> getAll(){
        return teacherService.getTeachers();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return teacherService.getById(id);
    }

    @DeleteMapping("deleteTeacher/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id){
        return teacherService.deleteTeacher(id);
    }

    @PutMapping("/updateTeacher/{id}")
    public ResponseEntity<?> updateStudent(@RequestBody TeacherDTO teacherDTO, @PathVariable Long id){
        return teacherService.updateTeacher(teacherDTO,id);
    }

    @PostMapping("/loginTeacher")
    public String loginTeacher(@RequestBody TeacherDTO teacherDTO){
        return teacherService.loginTeacher(teacherDTO);
    }

}
