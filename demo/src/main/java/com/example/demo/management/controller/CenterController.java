package com.example.demo.management.controller;

import com.example.demo.management.dto.CenterResponseDTO;
import com.example.demo.management.service.CenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/center")
@RequiredArgsConstructor
public class CenterController {

    private final CenterService centerService;

    @GetMapping("/by-subdomain/{subdomain}")
    public ResponseEntity<CenterResponseDTO> getBySubdomain(@PathVariable String subdomain) {
        return ResponseEntity.ok(centerService.getBySubdomain(subdomain));
    }
}
