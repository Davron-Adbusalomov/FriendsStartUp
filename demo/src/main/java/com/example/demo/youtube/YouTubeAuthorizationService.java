package com.example.demo.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YouTubeAuthorizationService {

    private final GoogleTokenService googleTokenService;

    public YouTube getYoutubeClient() throws Exception {

        String accessToken = googleTokenService.getAccessToken();

        return new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken)
        )
                .setApplicationName("Intellecta Resources")
                .build();
    }
}