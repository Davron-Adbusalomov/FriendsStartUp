package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.management.dto.CourseDTO;
import com.example.demo.management.mapper.CourseMapper;
import com.example.demo.management.model.Course;
import com.example.demo.management.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final PhotoService photoService;

    public Page<CourseDTO> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(courseMapper::toDto);
    }

    public CourseDTO getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        return courseMapper.toDto(course);
    }

    public CourseDTO createCourse(CourseDTO dto) throws IOException {
        Course course = courseMapper.toEntity(dto);
        course.setCenterId(TenantContext.getCenterId());
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            course.setImage(photoService.saveImage(dto.getImageFile(), "course"));
        }
        return courseMapper.toDto(courseRepository.save(course));
    }

    public CourseDTO updateCourse(UUID id, CourseDTO dto) throws IOException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        if (dto.getName() != null)             course.setName(dto.getName());
        if (dto.getDescription() != null)      course.setDescription(dto.getDescription());
        if (dto.getSubjectId() != null)        course.setSubjectId(dto.getSubjectId());
        if (dto.getDurationInMonths() != null) course.setDurationInMonths(dto.getDurationInMonths());
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            course.setImage(photoService.saveImage(dto.getImageFile(), "course"));
        }
        return courseMapper.toDto(courseRepository.save(course));
    }

    public void deleteCourse(UUID id) {
        courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        courseRepository.deleteById(id);
    }
}
