package com.example.demo.management.service;

import com.example.demo.management.dto.BadgeDTO;
import com.example.demo.management.dto.request.BadgeRequestDto;
import com.example.demo.management.mapper.BadgeMapper;
import com.example.demo.management.model.Badge;
import com.example.demo.management.repository.BadgeRepository;
import com.example.demo.utils.MessageSourceConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;
    private final MessageSource messageSource;

    public BadgeDTO create(BadgeRequestDto dto) {

        Badge badge = badgeMapper.toEntity(dto);

        Badge saved = badgeRepository.save(badge);

        return badgeMapper.toDto(saved);
    }

    public BadgeDTO update(UUID id, BadgeDTO dto) {

        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge not found"));

        badgeMapper.toEntity(dto);

        return badgeMapper.toDto(badgeRepository.save(badge));
    }

    public void delete(UUID id) {
        badgeRepository.deleteById(id);
    }

    public BadgeDTO assign(UUID badgeId, Long studentId) {

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));

        badge.setStudentId(studentId);

        return badgeMapper.toDto(badgeRepository.save(badge));
    }

    public List<BadgeDTO> getStudentBadges(Long studentId, Locale locale) {
        return badgeRepository.findByStudentId(studentId).stream()
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

    public String translate(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
