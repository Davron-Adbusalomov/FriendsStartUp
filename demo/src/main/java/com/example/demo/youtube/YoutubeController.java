package com.example.demo.youtube;


import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeUploadService youtubeUploadService;

    @PostMapping("/upload")
    public String upload(
            @RequestParam MultipartFile video,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "private") String privacy
    ) throws Exception {

        return youtubeUploadService.upload(
                video,
                title,
                description,
                privacy
        );
    }

}
