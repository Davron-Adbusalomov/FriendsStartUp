package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.AttachmentOwnerType;
import com.example.demo.enums.LessonProgressEnum;
import com.example.demo.management.dto.LessonDTO;
import com.example.demo.management.dto.LessonDetailsDTO;
import com.example.demo.management.dto.LessonProgressDTO;
import com.example.demo.management.dto.StudentLessonProgressDTO;
import com.example.demo.management.mapper.LessonMapper;
import com.example.demo.management.model.Lesson;
import com.example.demo.management.repository.LessonRepository;
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
    private final AttachmentService attachmentService;

    public LessonDTO create(LessonDTO dto) {
        Lesson lesson = lessonMapper.toEntity(dto);
        lesson.setCenterId(TenantContext.getCenterId());
        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    public LessonDTO update(UUID id, LessonDTO dto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (dto.getTitle() != null)         lesson.setTitle(dto.getTitle());
        if (dto.getDescription() != null)   lesson.setDescription(dto.getDescription());
        if (dto.getVideoUrl() != null)      lesson.setVideoUrl(dto.getVideoUrl());
        if (dto.getDuration() != null)      lesson.setDuration(dto.getDuration());
        if (dto.getOrderIndex() != null)    lesson.setOrderIndex(dto.getOrderIndex());
        if (dto.getInspectorName() != null) lesson.setInspectorName(dto.getInspectorName());
        if (dto.getInspectorInfo() != null) lesson.setInspectorInfo(dto.getInspectorInfo());
        if (dto.getCourseId() != null)      lesson.setCourseId(dto.getCourseId());
        if (dto.getCenterId() != null)      lesson.setCenterId(dto.getCenterId());

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    public LessonDetailsDTO getById(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        LessonDetailsDTO dto = lessonMapper.toDetailsDto(lesson);
        dto.setInspectorName(lesson.getInspectorName());
        dto.setInspectorInfo(lesson.getInspectorInfo());
        dto.setResources(attachmentService.getByOwner(AttachmentOwnerType.LESSON, id));

        return dto;
    }

    public Page<LessonDTO> getAll(String title, UUID courseId, Pageable pageable) {
        Specification<Lesson> spec = LessonSpecification.advancedFilter(title, courseId);
        Page<Lesson> page = lessonRepository.findAll(spec, pageable);
        return page.map(lessonMapper::toDto);
    }

    public void delete(UUID id) {
        lessonRepository.deleteById(id);
    }

    public List<StudentLessonProgressDTO> getLessonsWithStudentProgress(UUID courseId, Long studentId) {

        List<Lesson> lessons = lessonRepository.findAllByCourseIdOrderByOrderIndexAsc(courseId);

        List<LessonProgressDTO> progressList =
                lessonProgressService.getLessonProgressByStudentId(studentId);

        Set<UUID> completedLessonIds = progressList.stream()
                .map(LessonProgressDTO::getLessonId)
                .collect(Collectors.toSet());

        List<StudentLessonProgressDTO> result = new ArrayList<>();

        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            StudentLessonProgressDTO progressDTO = new StudentLessonProgressDTO();

            progressDTO.setId(lesson.getId());
            progressDTO.setTitle(lesson.getTitle());
            progressDTO.setDescription(lesson.getDescription());
            progressDTO.setVideoUrl(lesson.getVideoUrl());
            progressDTO.setDuration(lesson.getDuration());
            progressDTO.setOrderIndex(lesson.getOrderIndex());

            boolean isCompleted = completedLessonIds.contains(lesson.getId());
            if (isCompleted) {
                progressDTO.setLockingStatus(LessonProgressEnum.COMPLETED);
                result.add(progressDTO);
                continue;
            }

            if (i == 0) {
                progressDTO.setLockingStatus(LessonProgressEnum.UNLOCKED);
            } else {
                UUID prevLessonId = lessons.get(i - 1).getId();
                boolean prevCompleted = completedLessonIds.contains(prevLessonId);
                progressDTO.setLockingStatus(prevCompleted ? LessonProgressEnum.UNLOCKED : LessonProgressEnum.LOCKED);
            }

            result.add(progressDTO);
        }

        return result;
    }
}
