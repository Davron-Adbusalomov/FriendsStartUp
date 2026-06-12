package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.mapper.GroupMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.management.specification.GroupSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final GroupMapper groupMapper;
    private final LessonProgressService lessonProgressService;
    private final PhotoService photoService;

    public Page<GroupDTO> getGroups(Pageable pageable, Long teacherId, Long studentId, String name, String status) {
        Specification<Grouping> specification = GroupSpecification.advancedFilter(teacherId, studentId, name, status, TenantContext.getCenterId());

        Page<Grouping> groups = groupRepository.findAll(specification, pageable);

        Page<GroupDTO> groupDtos = groups.map(groupMapper::toDto);
        for(GroupDTO groupDto : groupDtos) {
            if (studentId != null) {
                double progressPercentage = lessonProgressService.calculateProgressPercentage(groupDto.getCourseId(), studentId);
                groupDto.setProgressPercentage(progressPercentage);
            }
        }
        return groupDtos;
    }

    public GroupDTO getGroupById(UUID groupId, Long studentId) {
        Grouping grouping = groupRepository.findByIdWithTeacher(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));
        GroupDTO group = groupMapper.toDto(grouping);
        if (studentId != null && grouping.getStudents().stream().anyMatch(s -> s.getId().equals(studentId))) {
            group.setIsStudentAccessible(true);
        }
        return group;
    }

    @Transactional
    public GroupDTO registerGroup(GroupDTO groupDTO) throws IOException {
        if (groupRepository.findByName(groupDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Group already existed!");
        }
        Grouping grouping = groupMapper.toEntity(groupDTO);
        grouping.setCenterId(TenantContext.getCenterId());

        if (groupDTO.getImageFile() != null && !groupDTO.getImageFile().isEmpty()) {
            grouping.setImage(photoService.saveImage(groupDTO.getImageFile(), "group"));
        }

        Grouping group = groupRepository.save(grouping);

        if (groupDTO.getTeacherId() != null) {
            AssignUserToGroupDTO assignUserToGroupDTO = new AssignUserToGroupDTO();
            assignUserToGroupDTO.setGroupId(group.getId());
            assignUserToGroupDTO.setId(groupDTO.getTeacherId());
            assignTeacherToGroup(assignUserToGroupDTO);
        }
        return groupMapper.toDto(group);
    }

    @Transactional
    public void deleteGroup(UUID groupId) {
        Grouping grouping = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        // break ManyToMany relation properly
        grouping.getStudents().forEach(student -> student.getGroupings().remove(grouping));
        grouping.getStudents().clear();

        groupRepository.delete(grouping);
    }

    public GroupDTO updateGroup(GroupDTO groupDTO, UUID groupId) throws IOException {
        Grouping grouping = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        if (groupDTO.getName() != null)
            grouping.setName(groupDTO.getName());
        if (groupDTO.getSubjectId() != null)
            grouping.setSubjectId(groupDTO.getSubjectId());
        if (groupDTO.getTime() != null)
            grouping.setTime(groupDTO.getTime());
        if (groupDTO.getDescription() != null)
            grouping.setDescription(groupDTO.getDescription());
        if (groupDTO.getStartDate() != null)
            grouping.setStartDate(groupDTO.getStartDate());
        if (groupDTO.getDurationInMonths() != null)
            grouping.setDurationInMonths(groupDTO.getDurationInMonths());
        if (groupDTO.getCourseId() != null)
            grouping.setCourseId(groupDTO.getCourseId());
        if (groupDTO.getImageFile() != null && !groupDTO.getImageFile().isEmpty()) {
            grouping.setImage(photoService.saveImage(groupDTO.getImageFile(), "group"));
        }

        groupRepository.saveAndFlush(grouping);
        return groupMapper.toDto(grouping);
    }

    public GroupDTO assignStudentToGroup(AssignUserToGroupDTO dto) {
        Student student = studentRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Grouping grouping = groupRepository.findById(dto.getGroupId()).orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (grouping.getStudents().contains(student)) {
            throw new IllegalStateException("Student already assigned to group");
        }

        grouping.assignStudent(student);
        groupRepository.save(grouping);
        return groupMapper.toDto(grouping);
    }

    public GroupDTO deassignStudentFromGroup(AssignUserToGroupDTO dto) {
        Student student = studentRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Grouping grouping = groupRepository.findById(dto.getGroupId()).orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (!grouping.getStudents().contains(student)) {
            throw new IllegalStateException("Student is not in this group");
        }

        grouping.deassignStudent(student);
        groupRepository.save(grouping);
        return groupMapper.toDto(grouping);
    }

    public GroupDTO assignTeacherToGroup(AssignUserToGroupDTO dto) {
        Teacher teacher = teacherRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        Grouping grouping = groupRepository.findById(dto.getGroupId()).orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (teacher.equals(grouping.getTeacher())) {
            throw new IllegalStateException("Teacher already assigned to group");
        }

        grouping.setTeacherId(teacher.getId());
        groupRepository.save(grouping);
        return groupMapper.toDto(grouping);
    }
}

