package com.example.demo.management.service;

import com.example.demo.config.JwtService;
import com.example.demo.management.dto.*;
import com.example.demo.management.mapper.AdminMapper;
import com.example.demo.management.mapper.GroupMapper;
import com.example.demo.management.mapper.StudentMapper;
import com.example.demo.management.mapper.TeacherMapper;
import com.example.demo.management.model.Admin;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.AdminRepository;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AdminService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public ArrayList<Admin> getAdmins(){
        return (ArrayList<Admin>) adminRepository.findAll();
    }

    public ResponseEntity<?> getAdminById(Long adminId){
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(()->new EntityNotFoundException("There is no admin with this id: "+adminId));
        return ResponseEntity.status(HttpStatus.OK).body(admin);
    }

    public AdminDTO addAdmin(AdminDTO adminDTO) throws Exception {
        if (adminRepository.findByUsername(adminDTO.getUsername()).isPresent()){
            throw new Exception("Username already taken!");
        }
        adminRepository.save(AdminMapper.INSTANCE.toModel(adminDTO));
        return adminDTO;
    }

    public ResponseEntity<?> deleteById(Long id){
        Admin admin = adminRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("There is no admin with this id: "+id));
        adminRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> updateAdmin(AdminDTO adminDTO, Long id){
        Admin admin = adminRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("There is no admin with this id: "+id));

        admin.setName(adminDTO.getName());
        admin.setUsername(adminDTO.getUsername());
        admin.setPassword(adminDTO.getPassword());

        Admin admin1=  adminRepository.save(admin);

        return ResponseEntity.status(HttpStatus.OK).body(admin1);
    }

    public ResponseEntity<?> registerStudent(StudentDTO studentDTO){
        if (studentRepository.findByUsername(studentDTO.getUsername()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already existed!");
        }
        Student student = studentRepository.save(StudentMapper.toModel(studentDTO));
        return ResponseEntity.status(HttpStatus.OK).body(student);
    }

    public ResponseEntity<?> registerTeacher(TeacherDTO teacherDTO){
        if (teacherRepository.findByUsername(teacherDTO.getUsername()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already existed!");
        }
        Teacher teacher = teacherRepository.save(TeacherMapper.INSTANCE.toModel(teacherDTO));
        return ResponseEntity.status(HttpStatus.OK).body(teacher);
    }

    public ResponseEntity<?> registerGroup(GroupDTO groupDTO){
        if (groupRepository.findByName(groupDTO.getName()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Group already existed!");
        }
        Grouping group = groupRepository.save(GroupMapper.INSTANCE.toModel(groupDTO));
        return ResponseEntity.status(HttpStatus.OK).body(group);
    }


    public AdminLoginDTO loginAdmin(AdminDTO adminDTO){
        try {
            if (adminRepository.findByUsername(adminDTO.getUsername()).isEmpty() || !Objects.equals(adminRepository.findByUsername(adminDTO.getUsername()).get().getPassword(), adminDTO.getPassword())){
                throw new EntityNotFoundException("There is no admin with this credentials!");
            }
            var admin = adminRepository.findByUsername(adminDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            String token = jwtService.generateToken(admin);

            AdminLoginDTO adminLoginDTO = new AdminLoginDTO();
            adminLoginDTO.setAdminDTO(AdminMapper.INSTANCE.toDTO(admin));
            adminLoginDTO.setToken(token);

            return adminLoginDTO;
        }
        catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }
}
