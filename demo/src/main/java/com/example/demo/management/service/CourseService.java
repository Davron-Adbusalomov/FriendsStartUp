package com.example.demo.management.service;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.config.TenantContext;
import com.example.demo.management.dto.CourseDTO;
import com.example.demo.management.mapper.CourseMapper;
import com.example.demo.management.model.Course;
import com.example.demo.management.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final PhotoService photoService;

    @Transactional(readOnly = true)
    public Page<CourseDTO> getCourses(Long teacherId, Pageable pageable) {
        UUID centerId = TenantContext.getCenterId();
        if (teacherId != null) {
            return (centerId != null
                    ? courseRepository.findByTeacherIdAndCenterId(teacherId, centerId, pageable)
                    : courseRepository.findByTeacherId(teacherId, pageable)
            ).map(courseMapper::toDto);
        }
        return (centerId != null
                ? courseRepository.findByCenterId(centerId, pageable)
                : courseRepository.findAll(pageable)
        ).map(courseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        return courseMapper.toDto(course);
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO dto) throws IOException {
        Course course = courseMapper.toEntity(dto);
        course.setCenterId(TenantContext.getCenterId());
        if (isCurrentUserTeacher()) {
            course.setTeacherId(CurrentUserUtils.getUserId());
        } else if (dto.getTeacherId() != null) {
            course.setTeacherId(dto.getTeacherId());
        }
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            course.setImage(photoService.saveImage(dto.getImageFile(), "course"));
        }
        return courseMapper.toDto(courseRepository.save(course));
    }

    @Transactional
    public CourseDTO updateCourse(UUID id, CourseDTO dto) throws IOException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        if (isCurrentUserTeacher() && !CurrentUserUtils.getUserId().equals(course.getTeacherId())) {
            throw new AccessDeniedException("You can only update your own courses");
        }
        if (dto.getName() != null)             course.setName(dto.getName());
        if (dto.getDescription() != null)      course.setDescription(dto.getDescription());
        if (dto.getSubjectId() != null)        course.setSubjectId(dto.getSubjectId());
        if (dto.getDurationInMonths() != null) course.setDurationInMonths(dto.getDurationInMonths());
        if (dto.getTeacherId() != null && !isCurrentUserTeacher()) course.setTeacherId(dto.getTeacherId());
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            course.setImage(photoService.saveImage(dto.getImageFile(), "course"));
        }
        return courseMapper.toDto(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        if (isCurrentUserTeacher() && !CurrentUserUtils.getUserId().equals(course.getTeacherId())) {
            throw new AccessDeniedException("You can only delete your own courses");
        }
        courseRepository.deleteById(id);
    }

    private boolean isCurrentUserTeacher() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("TEACHER"));
    }
}
