package com.example.demo.management.controller;

import com.example.demo.management.dto.SubjectDTO;
import com.example.demo.management.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(
            summary = "create subject",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('CREATE_SUBJECT')")
    @PostMapping("/create")
    public void create(@RequestBody SubjectDTO dto) {
        subjectService.create(dto);
    }

    @Operation(
            summary = "update subject",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('UPDATE_SUBJECT')")
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> update(
            @PathVariable UUID id,
            @RequestBody SubjectDTO dto
    ) {
        return ResponseEntity.ok(subjectService.update(id, dto));
    }

    @Operation(
            summary = "get subject by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('GET_SUBJECT')")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(subjectService.getById(id));
    }

    @Operation(
            summary = "get subjects list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('GET_SUBJECTS_LIST')")
    @GetMapping
    public Page<SubjectDTO> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            Pageable pageable
    ) {
        return subjectService.getAll(name, code, pageable);
    }

    @Operation(
            summary = "delete subject by id",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('DELETE_SUBJECT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
