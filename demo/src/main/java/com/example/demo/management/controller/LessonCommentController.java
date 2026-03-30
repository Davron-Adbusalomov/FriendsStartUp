package com.example.demo.management.controller;

import com.example.demo.config.TenantContext;
import com.example.demo.management.dto.LessonCommentDTO;
import com.example.demo.management.dto.request.LessonCommentCreateRequest;
import com.example.demo.management.service.LessonCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lesson-comments")
public class LessonCommentController {

    private final LessonCommentService lessonCommentService;

    /* GET list */
    @Operation(
            summary = "Get lesson comments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasAnyAuthority('GET_LESSON')")
    @GetMapping
    public List<LessonCommentDTO> getAll(@RequestParam(name = "lessonId") UUID lessonId) {
        return lessonCommentService.getAll(lessonId);
    }

    /* GET one */
    @Operation(summary = "Get lesson comment by id")
    @PreAuthorize("hasAnyAuthority('GET_LESSON')")
    @GetMapping("/{id}")
    public LessonCommentDTO getById(@PathVariable(name = "lessonId") UUID id) {
        return lessonCommentService.getById(id);
    }

    /* CREATE */
    @Operation(summary = "Create lesson comment")
    @PreAuthorize("hasAnyAuthority('GET_LESSON')")
    @PostMapping("/create")
    public LessonCommentDTO create(@RequestBody LessonCommentCreateRequest dto) {
        return lessonCommentService.create(dto);
    }

    /* UPDATE */
    @Operation(summary = "Update lesson comment")
    @PreAuthorize("hasAnyAuthority('GET_LESSON')")
    @PutMapping("/update/{id}")
    public LessonCommentDTO update(
            @PathVariable UUID id,
            @RequestBody LessonCommentDTO dto) {
        return lessonCommentService.update(id, dto);
    }

    /* DELETE (soft) */
    @Operation(summary = "Delete lesson comment")
    @PreAuthorize("hasAnyAuthority('GET_LESSON')")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
//        if(Contex)
        lessonCommentService.delete(id);
    }
}
