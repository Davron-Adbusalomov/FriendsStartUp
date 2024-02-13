package com.example.demo.management.service;

import com.example.demo.config.JwtService;
import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.dto.TeacherLoginDTO;
import com.example.demo.management.mapper.TeacherMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.test.model.Question;
import com.example.demo.test.model.Quiz;
import com.example.demo.test.repository.QuestionRepository;
import com.example.demo.test.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private final JwtService jwtService;

    public TeacherService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public List<TeacherDTO> getTeachers(){return TeacherMapper.toDTO(teacherRepository.findAll());}

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

        teacherRepository.delete(teacher);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted!");
    }

    public ResponseEntity<?> updateTeacher(TeacherDTO teacherDTO, Long id) throws Exception {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No teacher found with this id: "+id));

        if (teacher.getUsername().equals(teacherDTO.getUsername())){
            teacher.setName(teacherDTO.getName());
            teacher.setUsername(teacherDTO.getUsername());
            teacher.setSubject(teacherDTO.getSubject());
        }
        else {
            Optional<Teacher> teacher2 = teacherRepository.findByUsername(teacherDTO.getUsername());
            if (teacher2.isPresent()){
                throw new Exception("Username already taken");
            }else {
                teacher.setName(teacherDTO.getName());
                teacher.setUsername(teacherDTO.getUsername());
                teacher.setSubject(teacherDTO.getSubject());
            }
        }

        teacherRepository.save(teacher);
        return ResponseEntity.status(HttpStatus.OK).body(teacher);
    }

    public TeacherLoginDTO loginTeacher(TeacherDTO teacherDTO){
        try {
            if (teacherRepository.findByUsername(teacherDTO.getUsername()).isEmpty() || !Objects.equals(teacherRepository.findByUsername(teacherDTO.getUsername()).get().getPassword(), teacherDTO.getPassword())){
                throw new EntityNotFoundException("There is no teacher with this credentials!");
            }
            var teacher = teacherRepository.findByUsername(teacherDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            String token = jwtService.generateToken(teacher);

            TeacherLoginDTO teacherLoginDTO = new TeacherLoginDTO();
            teacherLoginDTO.setUser(TeacherMapper.toDTO(teacher));
            teacherLoginDTO.setToken(token);

            return teacherLoginDTO;
        }
        catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }

}
