package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.management.dto.BadgeDTO;
import com.example.demo.management.dto.request.BadgeRequestDto;
import com.example.demo.management.mapper.BadgeMapper;
import com.example.demo.management.model.Badge;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.BadgeRepository;
import com.example.demo.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final StudentRepository studentRepository;
    private final BadgeMapper badgeMapper;
    private final MessageSource messageSource;

    public BadgeDTO create(BadgeRequestDto dto) {
        Badge badge = badgeMapper.toEntity(dto);
        badge.setCenterId(TenantContext.getCenterId());
        return badgeMapper.toDto(badgeRepository.save(badge));
    }

    public BadgeDTO update(UUID id, BadgeDTO dto) {
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
        badge.setName(dto.getName());
        badge.setDescription(dto.getDescription());
        return badgeMapper.toDto(badgeRepository.save(badge));
    }

    public void delete(UUID id) {
        badgeRepository.deleteById(id);
    }

    public BadgeDTO assign(UUID badgeId, Long studentId) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (!badge.getStudents().contains(student)) {
            badge.getStudents().add(student);
            badgeRepository.save(badge);
        }
        return badgeMapper.toDto(badge);
    }

    public void unassign(UUID badgeId, Long studentId) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
        badge.getStudents().removeIf(s -> s.getId().equals(studentId));
        badgeRepository.save(badge);
    }

    public List<BadgeDTO> getAll(Locale locale) {
        return badgeRepository.findByCenterId(TenantContext.getCenterId()).stream()
                .map(badge -> {
                    BadgeDTO dto = badgeMapper.toDto(badge);
                    dto.setDescription(
                            messageSource.getMessage(badge.getDescription(), null, badge.getDescription(), locale)
                    );
                    return dto;
                })
                .toList();
    }

    public List<BadgeDTO> getStudentBadges(Long studentId, Locale locale) {
        return badgeRepository.findByStudentsId(studentId).stream()
                .map(badge -> {
                    BadgeDTO dto = badgeMapper.toDto(badge);
                    dto.setActive(true);
                    dto.setDescription(
                            messageSource.getMessage(badge.getDescription(), null, badge.getDescription(), locale)
                    );
                    return dto;
                })
                .toList();
    }

}
