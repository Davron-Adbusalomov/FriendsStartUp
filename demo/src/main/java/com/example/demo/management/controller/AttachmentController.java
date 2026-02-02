package com.example.demo.management.controller;

import com.example.demo.enums.AttachmentOwnerType;
import com.example.demo.management.dto.AttachmentDTO;
import com.example.demo.management.dto.request.UploadAttachmentRequest;
import com.example.demo.management.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(
            summary = "Upload attachment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('UPLOAD_ATTACHMENT')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentDTO upload(
            @RequestPart MultipartFile file,
            @RequestPart UploadAttachmentRequest request
    ) throws IOException {
        return attachmentService.upload(file, request);
    }

    /* ===== LIST ===== */
    @Operation(
            summary = "get attachments by owner",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('GET_ATTACHMENT')")
    @GetMapping
    public List<AttachmentDTO> list(
            @RequestParam AttachmentOwnerType ownerType,
            @RequestParam UUID ownerId
    ) {
        return attachmentService.getByOwner(ownerType, ownerId);
    }

    /* ===== DOWNLOAD ===== */
    @Operation(
            summary = "download attachment by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('DOWNLOAD_ATTACHMENT')")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        return attachmentService.download(id);
    }

    /* ===== DELETE ===== */
    @Operation(
            summary = "delete attachment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('DELETE_ATTACHMENT')")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        attachmentService.delete(id);
    }
}

