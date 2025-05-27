package com.example.demo.management.service;

import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.mapper.GroupMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.exam.model.Quiz;
import com.example.demo.exam.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private QuizRepository quizRepository;

    private final GroupMapper groupMapper;

    public List<Grouping> getGroups(){
        return groupRepository.findAll();
    }

    public List<Grouping> getGroupsByStudentId(Long studentId){
        return groupRepository.findByStudentId(studentId);
    }

    public GroupDTO registerGroup(GroupDTO groupDTO) throws Exception {
        if (groupRepository.findByName(groupDTO.getName()).isPresent()){
            throw new Exception("Group already existed!");
        }
        Grouping group = groupRepository.save(groupMapper.toEntity(groupDTO));
        return groupMapper.toDto(group);
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
//            grouping1.setQuizzes(groupDTO.getQuizzes());
            grouping1.setTime(groupDTO.getTime());

            groupRepository.save(grouping1);
            return ResponseEntity.status(HttpStatus.OK).body(grouping1);
        }
    }

    public ResponseEntity<?> assignStudentToGroup(AssignUserToGroupDTO dto) {
        Optional<Student> studentOpt = studentRepository.findById(dto.getId());
        Optional<Grouping> groupingOpt = groupRepository.findByName(dto.getGroupName());

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        if (groupingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
        }

        Student student = studentOpt.get();
        Grouping grouping = groupingOpt.get();

        if (grouping.getStudents().contains(student)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student already assigned to group");
        }

        grouping.assignStudent(student);
        return ResponseEntity.ok(groupRepository.save(grouping));
    }

    public ResponseEntity<?> deassignStudentFromGroup(AssignUserToGroupDTO dto) {
        Optional<Student> studentOpt = studentRepository.findById(dto.getId());
        Optional<Grouping> groupingOpt = groupRepository.findByName(dto.getGroupName());

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        if (groupingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
        }

        Student student = studentOpt.get();
        Grouping grouping = groupingOpt.get();

        if (!grouping.getStudents().contains(student)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student is not in this group");
        }

        grouping.deassignStudent(student);
        return ResponseEntity.ok(groupRepository.save(grouping));
    }

    public ResponseEntity<?> assignTeacherToGroup(AssignUserToGroupDTO dto) {
        Optional<Teacher> teacherOpt = teacherRepository.findById(dto.getId());
        Optional<Grouping> groupingOpt = groupRepository.findByName(dto.getGroupName());

        if (teacherOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Teacher not found");
        }

        if (groupingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
        }

        Teacher teacher = teacherOpt.get();
        Grouping grouping = groupingOpt.get();

        if (teacher.equals(grouping.getTeacher())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Teacher already assigned to group");
        }

        grouping.assignTeacher(teacher);
        return ResponseEntity.ok(groupRepository.save(grouping));
    }

}
