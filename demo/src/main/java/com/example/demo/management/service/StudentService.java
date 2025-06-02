package com.example.demo.management.service;

//import com.example.demo.config.JwtService;
import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.dto.TeacherInfoDTO;
import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.mapper.StudentMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.model.UserEntity;
import com.example.demo.management.model.rbac.RoleEntity;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.exam.model.Quiz_Results;
import com.example.demo.exam.repository.Quiz_ResultsRepository;
import com.example.demo.management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private Quiz_ResultsRepository quizResultsRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private final StudentMapper studentMapper;

//    private final JwtService jwtService;
//
//    public StudentService(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }

    public Page<StudentDTO> getStudents(Pageable pageable) {
        Page<Student> studentPage = studentRepository.findAll(pageable);

        List<StudentDTO> studentDTOs = studentPage.stream().map(student -> {
            StudentDTO dto = studentMapper.toDto(student);
            dto.setRoles(getRoles(student.getId()));
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(studentDTOs, pageable, studentPage.getTotalElements());
    }


    public StudentDTO getStudentById(Long studentID){
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("Not found student with id: "+studentID));
        StudentDTO studentDTO = studentMapper.toDto(student);
        studentDTO.setRoles(getRoles(studentDTO.getId()));
        return studentDTO;
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

        Optional<UserEntity> userEntity = userRepository.findById(studentId);
        userEntity.ifPresent(entity -> userRepository.delete(entity));

        studentRepository.delete(student);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted!");
    }


    public ResponseEntity<?> updateStudent(StudentDTO studentDTO, Long studentID) throws Exception {
        Optional<Student> studentOptional = studentRepository.findById(studentID);

        if (studentOptional.isEmpty()) {
            throw new EntityNotFoundException("Student not found with id: " + studentID);
        }

        Student student = studentOptional.get();

        student.setFullName(studentDTO.getFullName());
        student.setParent_contact(studentDTO.getParentContact());
        student.setPhoneNumber(studentDTO.getPhoneNumber());

        studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.OK).body(student);
    }

    private List<RolesEnum> getRoles(Long userId) {
        Set<RoleEntity> roles = userRepository.findRolesByUserId(userId);
        return roles == null ? Collections.emptyList() : roles.stream().map(RoleEntity::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

//    public StudentLoginDTO loginStudent(StudentDTO studentDTO) {
//        try {
//            if (studentRepository.findByUsername(studentDTO.getUsername()).isEmpty() || !Objects.equals(studentRepository.findByUsername(studentDTO.getUsername()).get().getPassword(), studentDTO.getPassword())){
//                throw new EntityNotFoundException("There is no student with this credentials!");
//            }
//            Student student = studentRepository.findByUsername(studentDTO.getUsername())
//                    .orElseThrow(() -> new RuntimeException("Student not found"));
//
//            StudentLoginDTO studentDTO1 = new StudentLoginDTO();
//            String token = jwtService.generateToken(student);
//            studentDTO1.setToken(token);
//            studentDTO1.setLoginTime(LocalDateTime.now());
//            studentDTO1.setUser(StudentMapper.toDTO(student));
//            return studentDTO1;
//        }
//        catch (Exception e) {
//            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
//        }
//    }
}
