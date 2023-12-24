package com.example.demo.management.service;

import com.example.demo.management.dto.TeacherDTO;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.test.model.Quiz;
import com.example.demo.test.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private QuizRepository quizRepository;

    public ResponseEntity<?> getTeachers(){return ResponseEntity.status(HttpStatus.OK).body(teacherRepository.findAll());}


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

        teacherRepository.save(teacher);
        return ResponseEntity.status(HttpStatus.OK).body(teacher);
    }

}
