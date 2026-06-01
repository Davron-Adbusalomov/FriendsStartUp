package com.example.demo.management.controller;

import com.example.demo.management.dto.LessonDTO;
import com.example.demo.management.dto.LessonDetailsDTO;
import com.example.demo.management.dto.StudentLessonProgressDTO;
import com.example.demo.management.service.LessonProgressService;
import com.example.demo.management.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final LessonProgressService lessonProgressService;

    @Operation(summary = "create lesson")
    @PreAuthorize("hasAuthority('CREATE_LESSON')")
    @PostMapping("/create")
    public ResponseEntity<LessonDTO> create(@RequestBody LessonDTO dto) {
        return ResponseEntity.ok(lessonService.create(dto));
    }

    @Operation(summary = "update lesson")
    @PreAuthorize("hasAuthority('UPDATE_LESSON')")
    @PutMapping("/update/{id}")
    public ResponseEntity<LessonDTO> update(@PathVariable UUID id, @RequestBody LessonDTO dto) {
        return ResponseEntity.ok(lessonService.update(id, dto));
    }

    @Operation(summary = "get lesson by id")
    @PreAuthorize("hasAuthority('GET_LESSON')")
    @GetMapping("/{id}")
    public ResponseEntity<LessonDetailsDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(lessonService.getById(id));
    }

    @Operation(summary = "get lessons list")
    @PreAuthorize("hasAuthority('GET_LESSONS_LIST')")
    @GetMapping
    public Page<LessonDTO> getAll(@RequestParam(required = false) UUID courseId,
                                  @RequestParam(required = false) String title,
                                  Pageable pageable) {
        return lessonService.getAll(title, courseId, pageable);
    }

    @Operation(summary = "delete lesson by id")
    @PreAuthorize("hasAuthority('DELETE_LESSON')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "get lessons with student progress by course")
    @PreAuthorize("hasAuthority('GET_LESSONS_LIST')")
    @GetMapping("/student-progress")
    public ResponseEntity<List<StudentLessonProgressDTO>> getByGroup(@RequestParam UUID courseId,
                                                                     @RequestParam Long studentId) {
        return ResponseEntity.ok(lessonService.getLessonsWithStudentProgress(courseId, studentId));
    }

    @Operation(summary = "mark lesson as completed")
    @PreAuthorize("hasAuthority('UPDATE_LESSON_PROGRESS')")
    @PostMapping("/complete-lesson")
    public ResponseEntity<Void> completeLesson(@RequestParam UUID lessonId,
                                               @RequestParam Long studentId) {
        lessonProgressService.markLessonAsCompleted(lessonId, studentId);
        return ResponseEntity.ok().build();
    }
}
