package com.example.demo.youtube;


import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final GoogleTokenService googleTokenService;

    /**
     * Frontend calls this (with its normal Authorization: Bearer <our JWT> header) to get a
     * live Google access token it can use directly against the YouTube Data API - e.g. for
     * uploading straight from the browser instead of proxying the file through our backend.
     * Requires authentication (this path is NOT in SecurityConfig's permitAll list), unlike
     * /api/youtube/auth/** which Google itself has to hit anonymously.
     * The token is short-lived (expiresIn seconds, normally 1 hour) - the frontend should
     * request a new one rather than caching it past that.
     */
    @GetMapping("/token")
    public YoutubeTokenResponse token() {
        return googleTokenService.getAccessTokenInfo();
    }

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
