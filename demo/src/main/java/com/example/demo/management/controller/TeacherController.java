package com.example.demo.management.controller;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.dto.TeacherLoginDTO;
import com.example.demo.management.service.TeacherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<?> loginTeacher(@RequestBody TeacherDTO teacherDTO, HttpServletResponse response){
        try{
            TeacherLoginDTO teacherLoginDTO = teacherService.loginTeacher(teacherDTO);
            Cookie cookie = new Cookie("jwt", teacherLoginDTO.getToken());
            cookie.setMaxAge(86400);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return ResponseEntity.status(HttpStatus.OK).body(teacherService.loginTeacher(teacherDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/logoutTeacher")
    public ResponseEntity<?> logoutTeacher(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie("jwt", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully logout!");
    }

}
