package com.example.demo.management.service;

import com.example.demo.management.dto.*;
import com.example.demo.management.mapper.*;
import com.example.demo.management.model.*;
import com.example.demo.management.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.catalina.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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


    public ArrayList<Admin> getAdmins(){
        return (ArrayList<Admin>) adminRepository.findAll();
    }

    public ResponseEntity<?> getAdminById(AdminDTO adminDTO){
        Admin admin = adminRepository.findById(adminDTO.getId())
                .orElseThrow(()->new EntityNotFoundException("There is no admin with this id: "+adminDTO.getId()));
        return ResponseEntity.status(HttpStatus.OK).body(admin);
    }

    public ResponseEntity<?> addAdmin(AdminDTO adminDTO){
        Admin admin = adminRepository.save(AdminMapper.INSTANCE.toModel(adminDTO));
        return ResponseEntity.status(HttpStatus.OK).body(admin);
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
        if (studentRepository.findByUsername(studentDTO.getName()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already existed!");
        }
        Student student = studentRepository.save(StudentMapper.INSTANCE.toModel(studentDTO));
        return ResponseEntity.status(HttpStatus.OK).body(student);
    }

    public ResponseEntity<?> registerTeacher(TeacherDTO teacherDTO){
        if (teacherRepository.findByUsername(teacherDTO.getName()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already existed!");
        }
        Teacher teacher = teacherRepository.save(TeacherMapper.INSTANCE.toModel(teacherDTO));
        return ResponseEntity.status(HttpStatus.OK).body(teacher);
    }

    public ResponseEntity<?> registerGroup(GroupDTO groupDTO){
        if (groupRepository.findByName(groupDTO.getName()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already existed!");
        }
        Grouping group = groupRepository.save(GroupMapper.INSTANCE.toModel(groupDTO));
        return ResponseEntity.status(HttpStatus.OK).body(group);
    }


    public ResponseEntity<?> loginAdmin(AdminDTO adminDTO){
        if (adminRepository.findByUsernameAndPassword(adminDTO.getUsername(), adminDTO.getPassword()).isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body("Successful login!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login failed! Admin credentials are wrong");
    }

}
