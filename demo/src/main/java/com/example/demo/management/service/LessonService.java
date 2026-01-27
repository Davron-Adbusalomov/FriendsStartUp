package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.LessonProgressEnum;
import com.example.demo.management.dto.LessonDTO;
import com.example.demo.management.dto.LessonProgressDTO;
import com.example.demo.management.dto.StudentLessonProgressDTO;
import com.example.demo.management.mapper.LessonMapper;
import com.example.demo.management.model.Lesson;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.LessonRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.management.specification.LessonSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final LessonProgressService lessonProgressService;
    private final TeacherRepository teacherRepository;

    public LessonDTO create(LessonDTO dto) {
        Lesson lesson = lessonMapper.toEntity(dto);
        lesson.setCenterId(TenantContext.getCenterId());
        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    public LessonDTO update(UUID id, LessonDTO dto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (dto.getTitle() != null)       lesson.setTitle(dto.getTitle());
        if (dto.getDescription() != null) lesson.setDescription(dto.getDescription());
        if (dto.getVideoUrl() != null)    lesson.setVideoUrl(dto.getVideoUrl());
        if (dto.getDuration() != null)    lesson.setDuration(dto.getDuration());
        if (dto.getOrderIndex() != null)  lesson.setOrderIndex(dto.getOrderIndex());
        if (dto.getGroupId() != null)     lesson.setGroupId(dto.getGroupId());
        if (dto.getCenterId() != null)    lesson.setCenterId(dto.getCenterId());

        lessonRepository.save(lesson);

        return lessonMapper.toDto(lesson);
    }

    public LessonDTO getById(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));


        LessonDTO dto = lessonMapper.toDto(lesson);

        if (lesson.getGrouping() != null
                && lesson.getGrouping().getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(lesson.getGrouping().getTeacherId())
                    .orElse(new Teacher());

            dto.setInspectorName(teacher.getFullName());
            dto.setInspectorInfo(teacher.getExperience());
        }

        return dto;
    }

    public Page<LessonDTO> getAll(String title, UUID groupId, Pageable pageable) {
        Specification<Lesson> spec = LessonSpecification.advancedFilter(title, groupId);

        Page<Lesson> page = lessonRepository.findAll(spec, pageable);
        return page.map(lessonMapper::toDto);
    }

    public void delete(UUID id) {
        lessonRepository.deleteById(id);
    }

    public List<StudentLessonProgressDTO> getLessonsWithStudentProgress(UUID groupId, Long studentId) {

        List<Lesson> lessons = lessonRepository.findAllByGroupIdOrderByOrderIndexAsc(groupId);

        List<LessonProgressDTO> progressList =
                lessonProgressService.getLessonProgressByStudentId(studentId);

        Set<UUID> completedLessonIds = progressList.stream()
                .map(LessonProgressDTO::getLessonId)
                .collect(Collectors.toSet());

        List<StudentLessonProgressDTO> result = new ArrayList<>();

        for (int i = 0; i < lessons.size(); i++) {

            Lesson lesson = lessons.get(i);
            StudentLessonProgressDTO dto = new StudentLessonProgressDTO();

            dto.setId(lesson.getId());
            dto.setTitle(lesson.getTitle());
            dto.setDescription(lesson.getDescription());
            dto.setVideoUrl(lesson.getVideoUrl());
            dto.setDuration(lesson.getDuration());
            dto.setOrderIndex(lesson.getOrderIndex());

            boolean isCompleted = completedLessonIds.contains(lesson.getId());
            if (isCompleted) {
                dto.setLockingStatus(LessonProgressEnum.COMPLETED);
                result.add(dto);
                continue;
            }

            if (i == 0) {
                dto.setLockingStatus(LessonProgressEnum.UNLOCKED);
            } else {
                UUID prevLessonId = lessons.get(i - 1).getId();
                boolean prevCompleted = completedLessonIds.contains(prevLessonId);

                if (prevCompleted) {
                    dto.setLockingStatus(LessonProgressEnum.UNLOCKED);
                } else {
                    dto.setLockingStatus(LessonProgressEnum.LOCKED);
                }
            }

            result.add(dto);
        }

        return result;
    }

}
