package com.example.demo.management.service;

import com.example.demo.exam.model.Quiz;
import com.example.demo.exam.repository.QuizRepository;
import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.mapper.GroupMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final QuizRepository quizRepository;
    private final GroupMapper groupMapper;

    public Page<GroupDTO> getGroups(Pageable pageable) {
        Page<Grouping> groups = groupRepository.findAll(pageable);
        return groups.map(groupMapper::toDto);
    }

    public GroupDTO getGroupById(Long groupId) {
        Grouping grouping = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));
        return groupMapper.toDto(grouping);
    }

    public GroupDTO registerGroup(GroupDTO groupDTO) {
        if (groupRepository.findByName(groupDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Group already existed!");
        }
        Grouping group = groupRepository.save(groupMapper.toEntity(groupDTO));

        if (groupDTO.getTeacherId() != null) {
            AssignUserToGroupDTO assignUserToGroupDTO = new AssignUserToGroupDTO();
            assignUserToGroupDTO.setGroupName(group.getName());
            assignUserToGroupDTO.setId(groupDTO.getTeacherId());
            assignTeacherToGroup(assignUserToGroupDTO);
        }
        return groupMapper.toDto(group);
    }

    public void deleteGroup(Long groupId) {
        Grouping grouping = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        // Remove students from groupings
        var groupings = studentRepository.findByGroupId(groupId);
        for (Grouping g : groupings) {
            for (Student student : g.getStudents()) {
                student.getGroupings().remove(g);
            }
        }

        // Remove quizzes association
        var quizzes = quizRepository.findByGroupingId(groupId);
        for (Quiz quiz : quizzes) {
            quiz.setGrouping(null);
        }

        groupRepository.delete(grouping);
    }

    public GroupDTO updateGroup(GroupDTO groupDTO, Long groupId) {
        Grouping grouping = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        grouping.setName(groupDTO.getName());
        grouping.setSubject(groupDTO.getSubject());
        grouping.setTime(groupDTO.getTime());
        // Note: handle quizzes if needed

        groupRepository.save(grouping);
        return groupMapper.toDto(grouping);
    }

    public GroupDTO assignStudentToGroup(AssignUserToGroupDTO dto) {
        Student student = studentRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Grouping grouping = groupRepository.findByName(dto.getGroupName()).orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (grouping.getStudents().contains(student)) {
            throw new IllegalStateException("Student already assigned to group");
        }

        grouping.assignStudent(student);
        groupRepository.save(grouping);
        return groupMapper.toDto(grouping);
    }

    public GroupDTO deassignStudentFromGroup(AssignUserToGroupDTO dto) {
        Student student = studentRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Grouping grouping = groupRepository.findByName(dto.getGroupName()).orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (!grouping.getStudents().contains(student)) {
            throw new IllegalStateException("Student is not in this group");
        }

        grouping.deassignStudent(student);
        groupRepository.save(grouping);
        return groupMapper.toDto(grouping);
    }

    public GroupDTO assignTeacherToGroup(AssignUserToGroupDTO dto) {
        Teacher teacher = teacherRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        Grouping grouping = groupRepository.findByName(dto.getGroupName()).orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (teacher.equals(grouping.getTeacher())) {
            throw new IllegalStateException("Teacher already assigned to group");
        }

        grouping.assignTeacher(teacher);
        groupRepository.save(grouping);
        return groupMapper.toDto(grouping);
    }
}

