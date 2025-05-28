package com.example.demo.management.controller;

import com.example.demo.management.dto.*;
import com.example.demo.management.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@Controller
@RequestMapping("api/v1/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

//    @PostMapping("/createADMIN")
//    public ResponseEntity<?> createAdmin(@RequestBody AdminDTO adminDTO){
//        try{
//            return ResponseEntity.status(HttpStatus.OK).body(adminService.addAdmin(adminDTO));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @PreAuthorize("hasAuthority('GET_ADMINS_LIST')")
    @GetMapping("/getAdmins")
    @Operation(
            summary = "Getting all admins",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    public ResponseEntity<?> getAdmins(Pageable pageable) {
        try {
            return ResponseEntity.ok(adminService.getAdmins(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "Getting admin by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    @PreAuthorize("hasAuthority('GET_ADMIN')")
    @GetMapping("/getById/{adminID}")
    public ResponseEntity<?> getById(@PathVariable Long adminID){
        return adminService.getAdminById(adminID);
    }

    @Operation(
            summary = "DELETE Admin by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('DELETE_ADMIN')")
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        return adminService.deleteById(id);
    }

    @Operation(
            summary = "UPDATING admin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('UPDATE_ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<?> updateAdmin(@RequestBody AdminDTO adminDTO) throws Exception {
        try {
            return adminService.updateAdmin(adminDTO);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/createSTUDENT")
//    public ResponseEntity<?> registerUser(@RequestBody StudentDTO studentDTO){
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(adminService.registerStudent(studentDTO));
//        }catch(Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

//    @CrossOrigin
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/createTEACHER")
//    public ResponseEntity<?> registerTeacher(@RequestBody TeacherDTO teacherDTO){
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(adminService.registerTeacher(teacherDTO));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/createGROUP")
//    public ResponseEntity<?> registerGroup(@RequestBody GroupDTO groupDTO){
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(adminService.registerGroup(groupDTO));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

//    @PostMapping("login")
//    public ResponseEntity<?> login(@RequestBody AdminDTO adminDTO, HttpServletResponse response){
//        try {
//            AdminLoginDTO adminLoginDTO = adminService.loginAdmin(adminDTO);
//            Cookie cookie = new Cookie("jwt", adminLoginDTO.getToken());
//            cookie.setPath("/");
//            cookie.setMaxAge(1000);
//            cookie.setHttpOnly(true);
//            response.addCookie(cookie);
//
//            return ResponseEntity.status(HttpStatus.OK).body(adminService.loginAdmin(adminDTO));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @PostMapping("/logout-admin")
    public ResponseEntity<?> logoutTeacher(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie("jwt", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully logout!");
    }

}
