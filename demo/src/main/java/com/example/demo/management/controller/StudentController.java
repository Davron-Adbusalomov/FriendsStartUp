package com.example.demo.management.controller;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/getStudents")
    public ResponseEntity<?> getAll(){
        return studentService.getStudents();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @DeleteMapping("deleteStudent/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id){
        return studentService.deleteStudent(id);
    }

    @PutMapping("/updateStudent/{id}")
    public ResponseEntity<?> updateStudent(@RequestBody StudentDTO studentDTO, @PathVariable Long id){
        return studentService.updateStudent(studentDTO,id);
    }
}
