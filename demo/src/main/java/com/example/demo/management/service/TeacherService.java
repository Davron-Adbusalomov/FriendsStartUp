package com.example.demo.management.service;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    public ResponseEntity<?> getTeachers(){return ResponseEntity.status(HttpStatus.OK).body(teacherRepository.findAll());}


    public ResponseEntity<?> getById(Long id){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No teacher found with this id: "+id));

        return ResponseEntity.status(HttpStatus.OK).body(teacher);
    }

    public ResponseEntity<?> deleteTeacher(Long id){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No teacher found with this id: "+id));

        teacherRepository.delete(teacher);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted!");
    }

    public ResponseEntity<?> updateTeacher(TeacherDTO teacherDTO, Long id){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No teacher found with this id: "+id));

        teacher.setName(teacherDTO.getName());
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setAge(teacherDTO.getAge());
        teacher.setUsername(teacherDTO.getUsername());
        teacher.setPassword(teacherDTO.getPassword());
        teacher.setPhone_num(teacherDTO.getPhone_num());
        //teacher.setGroupings(teacherDTO.getGroupDTOS());

        teacherRepository.save(teacher);
        return ResponseEntity.status(HttpStatus.OK).body(teacher);
    }


}
