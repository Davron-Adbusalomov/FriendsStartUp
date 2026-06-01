package com.example.demo.management.service;

import com.example.demo.config.CurrentUserUtils;
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
                .orElseGet(() -> {
                    LessonProgress newProgress = new LessonProgress();
                    newProgress.setLessonId(lessonId);
                    newProgress.setStudentId(studentId);
                    newProgress.setCenterId(CurrentUserUtils.getCenterId());
                    return newProgress;
                });
        lessonProgress.setCompleted(true);
        lessonProgress.setCompletedAt(new Date());
        lessonProgressRepository.save(lessonProgress);
    }

    public Double calculateProgressPercentage(UUID courseId, Long studentId) {
        long totalLessons =
                lessonProgressRepository.countByCourseId(courseId);

        long completedLessons =
                lessonProgressRepository
                        .countByCourseIdAndStudentIdAndCompletedTrue(courseId, studentId);

        if (totalLessons == 0) {
            return 0.0;
        }

        return (double) completedLessons / totalLessons * 100;
    }
}
