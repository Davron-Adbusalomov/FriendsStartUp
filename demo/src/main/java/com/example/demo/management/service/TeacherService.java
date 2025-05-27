package com.example.demo.management.service;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.dto.TeacherInfoDTO;
import com.example.demo.management.mapper.TeacherMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.model.UserEntity;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

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


    public List<TeacherDTO> getTeachers(){
        return teacherMapper.toDto(teacherRepository.findAll());
    }

    public ResponseEntity<?> getById(Long id){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No teacher found with this id: "+id));

        return ResponseEntity.status(HttpStatus.OK).body(teacher);
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

        teacher.setName(teacherDTO.getName());
        teacher.setExperience(teacherDTO.getExperience());
        teacher.setImage(teacherDTO.getImage());
        teacher.setSubject(teacherDTO.getSubject());
        teacher.setPhone_num(teacherDTO.getPhone_num());
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
            teacherInfoDTO.setName(teacher.getName());
            teacherInfoDTOS.add(teacherInfoDTO);
        }

        return teacherInfoDTOS;
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
