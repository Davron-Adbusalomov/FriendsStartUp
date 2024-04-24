package com.example.demo.test.controller;

import com.example.demo.test.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("api")
public class MediaController {

    @Autowired
    private MediaService mediaService;

//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadMedia(MultipartFile file) {
//        try {
//            String url = mediaService.uploadMedia(file);
//            return ResponseEntity.ok(url);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload media.");
//        }
//    }
}

