package com.example.demo.youtube;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * One-time setup flow for connecting the app's YouTube channel. Both endpoints here are
 * permitAll in SecurityConfig ("/api/youtube/auth/**") because Google redirects the admin's
 * browser straight to /callback with no way to attach our own JWT - see the README-style
 * walkthrough this controller's Javadoc leaves as breadcrumbs:
 *
 * 1. Admin opens GET /api/youtube/auth/authorize-url, copies the "url" it returns into a
 *    browser, and signs in with the Google account that owns the target YouTube channel.
 * 2. Google redirects back here to /callback?code=... with a one-time authorization code.
 * 3. /callback exchanges that code for an access_token + refresh_token pair.
 * 4. The admin copies the refresh_token out of that response and sets it as the
 *    YOUTUBE_REFRESH_TOKEN environment variable (then restarts the app).
 * 5. From then on, GoogleTokenService mints fresh access tokens from that refresh_token on
 *    every call - nobody has to repeat steps 1-4 unless the refresh token is revoked.
 */
@RestController
@RequestMapping("/api/youtube/auth")
public class YoutubeAuthController {

    @Value("${google.oauth.client-id:}")
    private String clientId;

    @Value("${google.oauth.client-secret:}")
    private String clientSecret;

    @Value("${google.oauth.redirect-uri:https://intellecta.uz/api/youtube/auth/callback}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Builds the Google consent-screen URL for step 1 above. access_type=offline + prompt=consent
     * are what makes Google actually hand back a refresh_token (it's omitted by default on repeat
     * consents). scope is youtube.upload - narrow on purpose: this token can publish videos to the
     * channel but can't read channel analytics, manage comments, etc.
     */
    @GetMapping("/authorize-url")
    public String authorizeUrl() {
        return UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("scope", "https://www.googleapis.com/auth/youtube.upload")
                .build()
                .toUriString();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> googleResponse = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                String.class
        );

        // Build our OWN response instead of returning googleResponse as-is: forwarding Google's
        // response headers verbatim (in particular its Transfer-Encoding: chunked) made Tomcat add
        // a second Transfer-Encoding header when it wrote our body, which nginx rejects outright
        // as a malformed upstream response (502 Bad Gateway) - even though the exchange with
        // Google itself had already succeeded. Only the body (containing the refresh_token the
        // admin needs to copy into YOUTUBE_REFRESH_TOKEN) matters here; this endpoint is a
        // one-time setup tool, not something the frontend calls day-to-day.
        return ResponseEntity.status(googleResponse.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(googleResponse.getBody());
    }
}
