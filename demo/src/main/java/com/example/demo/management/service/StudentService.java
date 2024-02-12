package com.example.demo.management.service;

import com.example.demo.config.JwtService;
import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.dto.StudentLoginDTO;
import com.example.demo.management.mapper.StudentMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.test.model.Quiz_Results;
import com.example.demo.test.repository.Quiz_ResultsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.loader.ast.spi.Loadable;
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

    @Autowired
    private Quiz_ResultsRepository quizResultsRepository;

    private final JwtService jwtService;

    public StudentService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public List<StudentDTO> getStudents(){return StudentMapper.toDTO(studentRepository.findAll());}

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

        List<Quiz_Results> quizResults = quizResultsRepository.findByStudentId(studentId);
        for (Quiz_Results quizResult: quizResults){
            quizResult.setStudent(null);
        }

        studentRepository.delete(student);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted!");
    }


    public ResponseEntity<?> updateStudent(StudentDTO studentDTO, Long studentID) throws Exception {
        Optional<Student> studentOptional = studentRepository.findById(studentID);

        if (studentOptional.isEmpty()){
            throw new EntityNotFoundException("Not found group with id: "+studentID);
        }

        Student student = studentOptional.get();

        if (studentOptional.get().getUsername().equals(studentDTO.getUsername())){

            student.setUsername(studentDTO.getUsername());
            student.setName(studentDTO.getName());
            student.setPassword(studentDTO.getPassword());
            student.setParent_contact(studentDTO.getParent_contact());

            studentRepository.save(student);
            return ResponseEntity.status(HttpStatus.OK).body(student);
        }
        else {
            Optional<Student> studentOptional1 = studentRepository.findByUsername(studentDTO.getUsername());
            if (studentOptional1.isPresent()){
                throw new Exception("Username already taken");
            }
            else {
                student.setUsername(studentDTO.getUsername());
                student.setName(studentDTO.getName());
                student.setPassword(studentDTO.getPassword());
                student.setParent_contact(studentDTO.getParent_contact());

                studentRepository.save(student);
                return ResponseEntity.status(HttpStatus.OK).body(student);
            }
        }
    }

    public StudentLoginDTO loginStudent(StudentDTO studentDTO) {
        try {
            if (studentRepository.findByUsername(studentDTO.getUsername()).isEmpty() || !Objects.equals(studentRepository.findByUsername(studentDTO.getUsername()).get().getPassword(), studentDTO.getPassword())){
                throw new EntityNotFoundException("There is no student with this credentials!");
            }
            Student student = studentRepository.findByUsername(studentDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            StudentLoginDTO studentDTO1 = new StudentLoginDTO();
            String token = jwtService.generateToken(student);
            studentDTO1.setToken(token);
            studentDTO1.setUser(StudentMapper.toDTO(student));
            return studentDTO1;
        }
        catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }
}
