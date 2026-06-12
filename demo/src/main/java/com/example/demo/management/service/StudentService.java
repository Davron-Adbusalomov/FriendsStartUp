package com.example.demo.management.service;

//import com.example.demo.config.JwtService;

import com.example.demo.exam.model.QuizResults;
import com.example.demo.exam.repository.QuizResultsRepository;
import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.dto.BadgeDTO;
import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.dto.StudentInfoDTO;
import com.example.demo.management.dto.StudentProfileDTO;
import com.example.demo.management.mapper.StudentMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.UserEntity;
import com.example.demo.management.model.rbac.RoleEntity;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.management.repository.UserRepository;
import com.example.demo.config.TenantContext;
import com.example.demo.management.specification.StudentSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    private final GroupRepository groupRepository;

    private final QuizResultsRepository quizResultsRepository;

    private final TeacherRepository teacherRepository;

    private final UserRepository userRepository;

    private final StudentMapper studentMapper;

    private final BadgeService badgeService;

    private final PhotoService photoService;

    private final PasswordEncoder passwordEncoder;

//    private final JwtService jwtService;
//
//    public StudentService(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }

    public Page<StudentDTO> getStudents(UUID groupId, Pageable pageable) {
        Specification<Student> spec = StudentSpecification.advancedFilter(groupId, TenantContext.getCenterId());

        Page<Student> studentPage = studentRepository.findAll(spec, pageable);

        return studentPage.map(student -> {
            StudentDTO dto = studentMapper.toDto(student);
            dto.setRoles(getRoles(student.getId()));
            dto.setIsBlocked(getIsBlocked(student.getId()));
            return dto;
        });
    }

    public StudentDTO getStudentById(Long studentID) {
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("Not found student with id: " + studentID));
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

        List<QuizResults> quizResults = quizResultsRepository.findByStudentId(studentId);
        for (QuizResults quizResult : quizResults) {
            quizResult.setStudent(null);
        }

        Optional<UserEntity> userEntity = userRepository.findById(studentId);
        userEntity.ifPresent(entity -> userRepository.delete(entity));

        studentRepository.delete(student);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted!");
    }

    @Transactional
    public ResponseEntity<?> updateStudent(StudentInfoDTO studentDTO, Long studentID) throws Exception {

        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("Not found student with id: " + studentID));

        Optional.ofNullable(studentDTO.getFullName()).ifPresent(student::setFullName);
        Optional.ofNullable(studentDTO.getParentContact()).ifPresent(student::setParentContact);
        Optional.ofNullable(studentDTO.getPhoneNumber()).ifPresent(student::setPhoneNumber);
        Optional.ofNullable(studentDTO.getEmail()).ifPresent(student::setEmail);

        String imageUrl = null;

        if (studentDTO.getImage() != null && !studentDTO.getImage().isEmpty()) {

            String contentType = studentDTO.getImage().getContentType();

            if (contentType == null ||
                    (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
                throw new IllegalArgumentException("Only PNG and JPEG images are allowed");
            }

            imageUrl = photoService.saveImage(studentDTO.getImage(), "student");
            student.setImage(imageUrl);
        }

        studentRepository.save(student);

        StudentDTO updatedStudent = studentMapper.toDto(student);
        updatedStudent.setRoles(getRoles(studentDTO.getId()));

        updateUser(studentDTO, studentID, imageUrl);
        return ResponseEntity.ok(updatedStudent);
    }

    private void updateUser(StudentInfoDTO studentDTO, Long studentID, String imageUrl) {
        UserEntity user = userRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + studentID));

        if (studentDTO.getPassword() != null && !studentDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        }

        if (imageUrl != null) {
            user.setImage(imageUrl);
        }
        userRepository.save(user);
    }

    private List<RolesEnum> getRoles(Long userId) {
        Set<RoleEntity> roles = userRepository.findRolesByUserId(userId);
        return roles == null ? Collections.emptyList() : roles.stream().map(RoleEntity::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Boolean getIsBlocked(Long userId) {
        return userRepository.findById(userId).map(UserEntity::getIsBlocked).orElse(false);
    }

    public StudentProfileDTO getStudentProfile(Long id, Locale locale) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        StudentProfileDTO profileDTO = new StudentProfileDTO();
        profileDTO.setId(student.getId());
        profileDTO.setFullName(student.getFullName());
        profileDTO.setImage(student.getImage());
        profileDTO.setGroupNames(student.getGroupings().stream().map(Grouping::getName).toList());
        profileDTO.setStatus("Top Student");
        List<BadgeDTO> badges = badgeService.getStudentBadges(student.getId(), locale);
        profileDTO.setBadges(badges);
        profileDTO.setCoursesCompleted(0);
        profileDTO.setAverageGrade("A");
        return profileDTO;
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
