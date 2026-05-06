package com.example.demo.management.controller;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.dto.TeacherInfoDTO;
import com.example.demo.management.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/teacher")
public class TeacherController {
    private final TeacherService teacherService;

    @PreAuthorize("hasAuthority('GET_TEACHERS_LIST')")
    @GetMapping()
    @Operation(
            summary = "Getting all teachers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid id"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Bad credential"),
                    @ApiResponse(responseCode = "403", description = "Access denied - Bad role permission"),
                    @ApiResponse(responseCode = "404", description = "Not found - Department not found"),
            })
    public Page<TeacherDTO> getAllTeachers(Pageable pageable) {
        return teacherService.getTeachers(pageable);
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
    @GetMapping("/{id}")
    public TeacherDTO getById(@PathVariable Long id) {
        return teacherService.getById(id);
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
    @DeleteMapping("delete/{id}")
    public void deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
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
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TeacherInfoDTO updateTeacher(@ModelAttribute TeacherInfoDTO teacherDTO, @PathVariable Long id) throws Exception {
        return teacherService.updateTeacher(teacherDTO, id);
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
    @PreAuthorize("hasAnyAuthority('GET_TEACHERS_LIST')")
    @GetMapping("info")
    public List<TeacherInfoDTO> getTeachersInfo() {
        return teacherService.getTeacherInfo();
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

//    @PostMapping("/logout-teacher")
//    public ResponseEntity<?> logoutTeacher(HttpServletResponse httpServletResponse){
//        Cookie cookie = new Cookie("jwt", "");
//        cookie.setMaxAge(0);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//        httpServletResponse.addCookie(cookie);
//        return ResponseEntity.status(HttpStatus.OK).body("Successfully logout!");
//    }

}
