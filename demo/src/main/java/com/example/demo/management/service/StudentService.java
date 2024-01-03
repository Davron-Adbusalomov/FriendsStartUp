package com.example.demo.management.service;

import com.example.demo.config.JwtService;
import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.mapper.StudentMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GroupRepository groupRepository;

    private final JwtService jwtService;

    public StudentService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> getStudents(){return ResponseEntity.status(HttpStatus.OK).body(studentRepository.findAll());}

    public ResponseEntity<?> getStudentById(Long studentID){
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("Not found student with id: "+studentID));
        return ResponseEntity.status(HttpStatus.OK).body(StudentMapper.toDTO(student));
    }

    public ResponseEntity<?> deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

        List<Grouping> groupings = groupRepository.findByStudentId(studentId);

        for (Grouping grouping : groupings) {
            grouping.getStudents().remove(student);
        }

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

    public String loginStudent(StudentDTO studentDTO) {
        try {
            if (studentRepository.findByUsername(studentDTO.getUsername()).isEmpty() || !Objects.equals(studentRepository.findByUsername(studentDTO.getUsername()).get().getPassword(), studentDTO.getPassword())){
                throw new EntityNotFoundException("There is no student with this credentials!");
            }
            var student = studentRepository.findByUsername(studentDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            return jwtService.generateToken(student);
        }
        catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }
}