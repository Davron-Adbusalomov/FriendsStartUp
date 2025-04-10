package com.example.demo.management.controller;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Operation(
            summary = "Getting all students",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('GET_STUDENTS_LIST')")
    @GetMapping("/getStudents")
    public ResponseEntity<?> getAll(){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(studentService.getStudents());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Getting student by student",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('GET_STUDENT')")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @Operation(
            summary = "DELETE STUDENT by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('DELETE_STUDENT')")
    @DeleteMapping("deleteStudent/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id){
        return studentService.deleteStudent(id);
    }

    @Operation(
            summary = "UPDATING student by student",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('UPDATE_STUDENT')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStudent(@RequestBody StudentDTO studentDTO, @PathVariable Long id){
        try {
            return studentService.updateStudent(studentDTO,id);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @PostMapping("loginStudent")
//    public ResponseEntity<?> loginStudent(@RequestBody StudentDTO studentDTO, HttpServletResponse response){
//        try {
//            StudentLoginDTO loginDTO = studentService.loginStudent(studentDTO);
//
//            Cookie cookie = new Cookie("jwt", loginDTO.getToken());
//            cookie.setMaxAge(1000);
//            cookie.setPath("/");
//            cookie.setHttpOnly(true);
//            response.addCookie(cookie);
//
//            return ResponseEntity.status(HttpStatus.OK).body(studentService.loginStudent(studentDTO));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

//    @PostMapping("/logout-student")
//    public ResponseEntity<?> logoutStudent(HttpServletResponse httpServletResponse){
//        Cookie cookie = new Cookie("jwt", "");
//        cookie.setMaxAge(0);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//        httpServletResponse.addCookie(cookie);
//        return ResponseEntity.status(HttpStatus.OK).body("Successfully logout!");
//    }

}
