package com.example.demo.management.service;

import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
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
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private QuizRepository quizRepository;

    public List<Grouping> getGroups(){
        return groupRepository.findAll();
    }

    public ResponseEntity<?> getGroupById(Long groupID){
        Grouping grouping = groupRepository.findById(groupID)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: "+groupID));
        return ResponseEntity.status(HttpStatus.OK).body(grouping);
    }

    public ResponseEntity<?> deleteGroup(Long groupId) {
        Grouping grouping = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: " + groupId));

        List<Grouping> groupings = studentRepository.findByGroupId(groupId);

        for (Grouping grouping1 : groupings) {
            for (Student student : grouping1.getStudents()) {
                student.getGroupings().remove(grouping1);
            }
        }

        List<Quiz> quizzes = quizRepository.findByGroupingId(groupId);

        for (Quiz quiz: quizzes) {
            quiz.setGrouping(null);
        }

        groupRepository.delete(grouping);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted!");
    }


    public ResponseEntity<?> updateGroup(GroupDTO groupDTO, Long groupId){
        Optional<Grouping> group =groupRepository.findById(groupId);
        if(group.isEmpty()){
            throw new EntityNotFoundException("Not found group with id: "+groupId);
        }
        else{
            Grouping grouping1 = group.get();
            grouping1.setId(groupId);
            grouping1.setName(groupDTO.getName());
            grouping1.setSubject(groupDTO.getSubject());
            grouping1.setQuizzes(groupDTO.getQuizzes());

            groupRepository.save(grouping1);
            return ResponseEntity.status(HttpStatus.OK).body(grouping1);
        }
    }

    public ResponseEntity<?> assignStudentToGroup(AssignUserToGroupDTO assignUserToGroupDTO){
        Student student = studentRepository.findByUsername(assignUserToGroupDTO.getUsername()).get();
        Grouping grouping = groupRepository.findByName(assignUserToGroupDTO.getGroupName()).get();
        grouping.assignStudent(student);
        return ResponseEntity.status(HttpStatus.OK).body(groupRepository.save(grouping));
    }

    public ResponseEntity<?> deassignStudentFromGroup(AssignUserToGroupDTO assignUserToGroupDTO){
        Student student = studentRepository.findByUsername(assignUserToGroupDTO.getUsername()).get();
        Grouping grouping = groupRepository.findByName(assignUserToGroupDTO.getGroupName()).get();
        grouping.deassignStudent(student);
        return ResponseEntity.status(HttpStatus.OK).body(groupRepository.save(grouping));
    }

    public ResponseEntity<?> assignTeacherToGroup(AssignUserToGroupDTO assignUserToGroupDTO){
        Teacher teacher = teacherRepository.findByUsername(assignUserToGroupDTO.getUsername()).get();
        Grouping grouping = groupRepository.findByName(assignUserToGroupDTO.getGroupName()).get();
        grouping.assignTeacher(teacher);
        return ResponseEntity.status(HttpStatus.OK).body(groupRepository.save(grouping));
    }
}
