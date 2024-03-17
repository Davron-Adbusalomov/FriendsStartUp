package com.example.demo.management.controller;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.dto.StudentLoginDTO;
import com.example.demo.management.model.Student;
import com.example.demo.management.service.StudentService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/getStudents")
    public ResponseEntity<?> getAll(){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(studentService.getStudents());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @DeleteMapping("deleteStudent/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id){
        return studentService.deleteStudent(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStudent(@RequestBody StudentDTO studentDTO, @PathVariable Long id){
        try {
            return studentService.updateStudent(studentDTO,id);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("loginStudent")
    public ResponseEntity<?> loginStudent(@RequestBody StudentDTO studentDTO, HttpServletResponse response){
        try {
            StudentLoginDTO loginDTO = studentService.loginStudent(studentDTO);

            Cookie cookie = new Cookie("jwt", loginDTO.getToken());
            cookie.setMaxAge(86400);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return ResponseEntity.status(HttpStatus.OK).body(studentService.loginStudent(studentDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/logoutStudent")
    public ResponseEntity<?> logoutTeacher(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie("jwt", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully logout!");
    }

    @GetMapping("getTeachersInfo")
    public ResponseEntity<?> getTeachersInfo(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(studentService.getTeacherInfo());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
