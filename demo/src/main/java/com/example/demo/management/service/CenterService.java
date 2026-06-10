package com.example.demo.management.service;

import com.example.demo.management.dto.CenterResponseDTO;
import com.example.demo.management.model.Center;
import com.example.demo.management.repository.CenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CenterService {

    private final CenterRepository centerRepository;

    public CenterResponseDTO getBySubdomain(String subdomain) {
        Center center = centerRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Center not found"));
        return new CenterResponseDTO(center.getId(), center.getName(), center.getSubdomain(), center.getLogo());
    }
}
