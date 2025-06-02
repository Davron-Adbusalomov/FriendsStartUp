package com.example.demo.management.service;

import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.dto.TeacherInfoDTO;
import com.example.demo.management.mapper.TeacherMapper;
import com.example.demo.management.model.Admin;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.model.UserEntity;
import com.example.demo.management.model.rbac.RoleEntity;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.exam.model.Question;
import com.example.demo.exam.model.Quiz;
import com.example.demo.exam.repository.QuestionRepository;
import com.example.demo.exam.repository.QuizRepository;
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
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    private final TeacherMapper teacherMapper;


    public Page<TeacherDTO> getTeachers(Pageable pageable) {
        Page<Teacher> teacherPage = teacherRepository.findAll(pageable);

        List<TeacherDTO> teacherDTOs = teacherPage.stream().map(teacher -> {
            TeacherDTO dto = teacherMapper.toDto(teacher);
            dto.setRoles(getRoles(teacher.getId()));
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(teacherDTOs, pageable, teacherPage.getTotalElements());
    }


    public TeacherDTO getById(Long id){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No teacher found with this id: "+id));

        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);
        teacherDTO.setRoles(getRoles(teacherDTO.getId()));
        return teacherDTO;
    }

    public ResponseEntity<?> deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No teacher found with this id: " + id));

        List<Grouping> groupings = groupRepository.findByTeacherId(id);

        for (Grouping grouping : groupings) {
            grouping.setTeacher(null);
        }

        List<Quiz> quizzes = quizRepository.findByTeacherId(id);

        for (Quiz quiz: quizzes) {
            quiz.setTeacher(null);
        }

        List<Question> questions = questionRepository.findByTeacherId(id);
        for (Question question:questions){
            question.setTeacher(null);
        }

        Optional<UserEntity> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(userEntity -> userRepository.delete(userEntity));

        teacherRepository.delete(teacher);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted!");
    }

    public ResponseEntity<?> updateTeacher(TeacherDTO teacherDTO, Long id) throws Exception {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No teacher found with this id: " + id));

        teacher.setFullName(teacherDTO.getFullName());
        teacher.setExperience(teacherDTO.getExperience());
        teacher.setImage(teacherDTO.getImage());
        teacher.setSubject(teacherDTO.getSubject());
        teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        teacher.setExperience(teacherDTO.getExperience());

        teacherRepository.save(teacher);
        return ResponseEntity.status(HttpStatus.OK).body(teacher);
    }


    public List<TeacherInfoDTO> getTeacherInfo(){
        List<Teacher> teachers=teacherRepository.findAll();
        List<TeacherInfoDTO> teacherInfoDTOS = new ArrayList<>();

        for (Teacher teacher:teachers) {

            TeacherInfoDTO teacherInfoDTO = new TeacherInfoDTO();
            teacherInfoDTO.setId(teacher.getId());
            teacherInfoDTO.setSubject(teacher.getSubject());
            teacherInfoDTO.setExperience(teacher.getExperience());
            teacherInfoDTO.setImage(teacher.getImage());
            teacherInfoDTO.setName(teacher.getFullName());
            teacherInfoDTOS.add(teacherInfoDTO);
        }

        return teacherInfoDTOS;
    }

    private List<RolesEnum> getRoles(Long userId) {
        Set<RoleEntity> roles = userRepository.findRolesByUserId(userId);
        return roles == null ? Collections.emptyList() : roles.stream().map(RoleEntity::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

//    public TeacherLoginDTO loginTeacher(TeacherDTO teacherDTO){
//        try {
//            if (teacherRepository.findByUsername(teacherDTO.getUsername()).isEmpty() || !Objects.equals(teacherRepository.findByUsername(teacherDTO.getUsername()).get().getPassword(), teacherDTO.getPassword())){
//                throw new EntityNotFoundException("There is no teacher with this credentials!");
//            }
//            var teacher = teacherRepository.findByUsername(teacherDTO.getUsername())
//                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
//
//            String token = jwtService.generateToken(teacher);
//
//            TeacherLoginDTO teacherLoginDTO = new TeacherLoginDTO();
//            teacherLoginDTO.setUser(TeacherMapper.toDTO(teacher));
//            teacherLoginDTO.setToken(token);
//
//            return teacherLoginDTO;
//        }
//        catch (Exception e) {
//            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
//        }
//    }

}
