package com.example.demo.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleTokenService {

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.refresh-token}")
    private String refreshToken;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getAccessToken() throws Exception {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        org.springframework.http.HttpHeaders headers =
                new org.springframework.http.HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        org.springframework.http.HttpEntity<MultiValueMap<String, String>> entity =
                new org.springframework.http.HttpEntity<>(body, headers);

        String response = restTemplate.postForObject(
                "https://oauth2.googleapis.com/token",
                entity,
                String.class
        );

        JsonNode json = objectMapper.readTree(response);

        return json.get("access_token").asText();
    }
}
