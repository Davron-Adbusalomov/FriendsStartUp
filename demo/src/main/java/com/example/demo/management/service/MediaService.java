package com.example.demo.management.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MediaService {
    @Value("${app.attachments.path}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();

        String extension;
        if ("image/png".equals(contentType)) {
            extension = ".png";
        } else if ("image/jpeg".equals(contentType)) {
            extension = ".jpg";
        } else {
            throw new IllegalArgumentException("Only PNG and JPEG allowed");
        }

        String fileName = UUID.randomUUID() + extension;

        Path path = Paths.get(uploadDir, fileName);

        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return baseUrl + "/attachments/" + fileName;
    }
}
