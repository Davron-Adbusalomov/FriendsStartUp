package com.example.demo.management.service;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class StudentService {
    @Autowired
    StudentRepository studentRepository;

    public ResponseEntity<?> getStudents(){return ResponseEntity.status(HttpStatus.OK).body(studentRepository.findAll());}

    public ResponseEntity<?> getStudentById(Long studentID){
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("Not found student with id: "+studentID));
        return ResponseEntity.status(HttpStatus.OK).body(student);
    }

    public ResponseEntity<?> deleteStudent(Long studentID){
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: "+studentID));
        studentRepository.delete(student);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted!");
    }

    public ResponseEntity<?> updateStudent(StudentDTO studentDTO, Long studentID){
        Optional<Student> studentOptional = studentRepository.findById(studentID);
        if (studentOptional.isEmpty()){
            throw new EntityNotFoundException("Not found group with id: "+studentID);
        }
        else {
            Student student = studentOptional.get();
            student.setId(studentDTO.getId());
            student.setUsername(studentDTO.getUsername());
            student.setName(studentDTO.getName());
            student.setRole(studentDTO.getRole());
            student.setEmail(studentDTO.getEmail());
            student.setNumber(studentDTO.getNumber());
            student.setPassword(studentDTO.getPassword());
            student.setAge(studentDTO.getAge());

            studentRepository.save(student);
            return ResponseEntity.status(HttpStatus.OK).body(student);
        }
    }

}