package com.example.demo.management.service;

import com.example.demo.enums.AttachmentOwnerType;
import com.example.demo.management.dto.AttachmentDTO;
import com.example.demo.management.dto.request.UploadAttachmentRequest;
import com.example.demo.management.model.Attachment;
import com.example.demo.management.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Value("${app.attachments.path}")
    private String attachmentsPath;

    @Value("${app.base-url}")
    private String baseUrl;

    /* ================= UPLOAD ================= */

    public AttachmentDTO upload(
            MultipartFile file,
            UploadAttachmentRequest request
    ) throws IOException {

        // 1. Generate stored name
        String extension = getExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + extension;

        // 2. Save file to disk
        Path targetPath = Paths.get(attachmentsPath, storedName);
        Files.createDirectories(targetPath.getParent());
        file.transferTo(targetPath);

        // 3. Save DB record
        Attachment attachment = new Attachment();
        attachment.setOriginalName(file.getOriginalFilename());
        attachment.setStoredName(storedName);
        attachment.setTitle(request.getTitle());
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setType(request.getType());
        attachment.setOwnerType(request.getOwnerType());
        attachment.setOwnerId(request.getOwnerId());
        attachment.setUploadedAt(LocalDateTime.now());

        attachmentRepository.save(attachment);

        return toDto(attachment);
    }

    /* ================= LIST ================= */

    public List<AttachmentDTO> getByOwner(
            AttachmentOwnerType ownerType,
            UUID ownerId
    ) {
        return attachmentRepository
                .findAllByOwnerTypeAndOwnerId(ownerType, ownerId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /* ================= DOWNLOAD ================= */

    public ResponseEntity<Resource> download(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

//        Path path = Paths.get(attachmentsPath, attachment.getStoredName());
        Path path = Paths.get("https://www.orimi.com/pdf-test.pdf");

        if (!Files.exists(path)) {
            throw new RuntimeException("File missing on disk");
        }

        FileSystemResource resource = new FileSystemResource(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getOriginalName() + "\"")
                .body(resource);
    }


    public void delete(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        attachmentRepository.delete(attachment);
        // file deletion is optional (usually async or scheduled)
    }


    private AttachmentDTO toDto(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setTitle(attachment.getTitle());
        dto.setOriginalName(attachment.getOriginalName());
        dto.setUrl(
                baseUrl + "/api/v1/attachments/" + attachment.getId() + "/download"
        );
        return dto;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
