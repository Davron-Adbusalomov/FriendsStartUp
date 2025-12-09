package com.example.demo.management.service;

import com.example.demo.management.dto.LessonProgressDTO;
import com.example.demo.management.mapper.LessonProgressMapper;
import com.example.demo.management.model.LessonProgress;
import com.example.demo.management.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonProgressService {
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonProgressMapper lessonProgressMapper;

    public List<LessonProgressDTO> getLessonProgressByStudentId(Long studentId) {
        List<LessonProgress> lessonProgresses = lessonProgressRepository.findLessonProgressByStudentId(studentId);
        return lessonProgresses.stream()
                .map(lessonProgressMapper::toDto)
                .toList();
    }

    public void markLessonAsCompleted(UUID lessonId, Long studentId) {
        LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndStudentId(lessonId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson progress not found for lessonId: " + lessonId + " and studentId: " + studentId));
        lessonProgress.setCompleted(true);
        lessonProgress.setCompletedAt(new Date());
        lessonProgressRepository.save(lessonProgress);
    }
}
