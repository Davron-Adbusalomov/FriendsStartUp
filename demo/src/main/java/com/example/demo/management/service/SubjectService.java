package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.management.dto.SubjectDTO;
import com.example.demo.management.mapper.SubjectMapper;
import com.example.demo.management.model.Subject;
import com.example.demo.management.repository.SubjectRepository;
import com.example.demo.management.specification.SubjectSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public void create(SubjectDTO subjectDTO) {
        Subject subject = subjectMapper.toEntity(subjectDTO);
        subject.setCenterId(TenantContext.getCenterId());
        subjectRepository.save(subject);
    }

    public SubjectDTO getById(UUID id) {
        return subjectMapper.toDto(subjectRepository.findById(id).orElseThrow(() -> new
                EntityNotFoundException("Subject not found with id: " + id)));
    }

    public Page<SubjectDTO> getAll(String name, String code, Pageable pageable) {
        Specification<Subject> specification = SubjectSpecification.advancedFilter(name, code);

        Page<Subject> subjects = subjectRepository.findAll(specification, pageable);
        return subjects.map(subjectMapper::toDto);
    }

    public void delete(UUID id) {
        subjectRepository.deleteById(id);
    }

    public SubjectDTO update(UUID id, SubjectDTO subjectDTO) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new
                EntityNotFoundException("Subject not found with id: " + id));
        if (subjectDTO.getName() != null) {
            subject.setName(subjectDTO.getName());
        }
        if (subjectDTO.getDescription() != null) {
            subject.setDescription(subjectDTO.getDescription());
        }
        if (subjectDTO.getCode() != null) {
            subject.setCode(subjectDTO.getCode());
        }
        subjectRepository.save(subject);
        return subjectMapper.toDto(subject);
    }
}
