package com.example.demo.management.controller;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @Operation(
            summary = "Getting all teachers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('GET_TEACHERS_LIST')")
    @GetMapping("/getTeachers")
    public ResponseEntity<?> getAll(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(teacherService.getTeachers());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Getting student by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('GET_TEACHER')")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.getById(id));
    }

    @Operation(
            summary = "DELETE TEACHER by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('DELETE_TEACHER')")
    @DeleteMapping("deleteTeacher/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id){
        return teacherService.deleteTeacher(id);
    }

    @Operation(
            summary = "UPDATING teacher",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('UPDATE_TEACHER')")
    @PutMapping("/updateTeacher/{id}")
    public ResponseEntity<?> updateTeacher(@RequestBody TeacherDTO teacherDTO, @PathVariable Long id){
        try {
            return teacherService.updateTeacher(teacherDTO,id);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Get teacher info",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('')")
    @GetMapping("getTeachersInfo")
    public ResponseEntity<?> getTeachersInfo(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(teacherService.getTeacherInfo());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @PostMapping("/loginTeacher")
//    public ResponseEntity<?> loginTeacher(@RequestBody TeacherDTO teacherDTO, HttpServletResponse response){
//        try{
//            TeacherLoginDTO teacherLoginDTO = teacherService.loginTeacher(teacherDTO);
//            Cookie cookie = new Cookie("jwt", teacherLoginDTO.getToken());
//            cookie.setMaxAge(1000);
//            cookie.setPath("/");
//            cookie.setHttpOnly(true);
//            response.addCookie(cookie);
//
//            return ResponseEntity.status(HttpStatus.OK).body(teacherService.loginTeacher(teacherDTO));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @PostMapping("/logout-teacher")
    public ResponseEntity<?> logoutTeacher(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie("jwt", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully logout!");
    }

}
