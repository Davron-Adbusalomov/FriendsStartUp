package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.management.dto.LessonCommentDTO;
import com.example.demo.management.dto.request.LessonCommentCreateRequest;
import com.example.demo.management.mapper.LessonCommentMapper;
import com.example.demo.management.model.LessonComment;
import com.example.demo.management.repository.LessonCommentRepository;
import com.example.demo.utils.ProjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonCommentService {

    private final LessonCommentRepository lessonCommentRepository;
    private final LessonCommentMapper lessonCommentMapper;

    public List<LessonCommentDTO> getAll(UUID lessonId) {
        return lessonCommentRepository.findAllByLessonId(lessonId)
                .stream()
                .map(lessonCommentMapper::toDto)
                .toList();
    }

    public LessonCommentDTO getById(UUID id) {
        LessonComment comment = lessonCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson comment not found"));
        return lessonCommentMapper.toDto(comment);
    }

    public LessonCommentDTO create(LessonCommentCreateRequest dto) {
        LessonComment comment = new LessonComment();
        comment.setUserId(dto.getUserId());
        comment.setLessonId(dto.getLessonId());
        comment.setContent(dto.getContent());
        comment.setCenterId(TenantContext.getCenterId());

        lessonCommentRepository.save(comment);
        return lessonCommentMapper.toDto(comment);
    }

    public LessonCommentDTO update(UUID id, LessonCommentDTO dto) {
        LessonComment comment = lessonCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson comment not found"));

        Long currentUserId = Objects.requireNonNull(ProjectUtils.getCurrentUserDetails()).getId();
        if (!Objects.equals(comment.getUserId(), currentUserId)) {
            throw new RuntimeException("Only comment owner can delete this comment!");
        }

        comment.setContent(dto.getContent());
        lessonCommentRepository.save(comment);

        return lessonCommentMapper.toDto(comment);
    }

    public void delete(UUID id) {
        lessonCommentRepository.deleteById(id);
    }
}
