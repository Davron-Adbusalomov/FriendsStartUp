package com.example.demo.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Turns the long-lived refresh token we obtained once (see YoutubeAuthController) into
 * short-lived Google OAuth2 access tokens, on demand.
 *
 * These three values come from application.yml -> google.oauth.* which is backed by the
 * YOUTUBE_CLIENT_ID / YOUTUBE_CLIENT_SECRET / YOUTUBE_REFRESH_TOKEN environment variables.
 * They default to blank so the app still boots without them configured; calling
 * getAccessToken()/getAccessTokenInfo() before they're set fails fast with a clear message
 * instead of a confusing NullPointerException or a Google 400.
 */
@Service
public class GoogleTokenService {

    @Value("${google.oauth.client-id:}")
    private String clientId;

    @Value("${google.oauth.client-secret:}")
    private String clientSecret;

    @Value("${google.oauth.refresh-token:}")
    private String refreshToken;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @return a fresh access token string. Kept for internal callers (YouTubeAuthorizationService)
     * that only care about the Bearer value, not its expiry.
     */
    public String getAccessToken() {
        return getAccessTokenInfo().accessToken();
    }

    /**
     * @return a fresh access token plus how many seconds it is valid for - what the
     * /api/youtube/token endpoint hands back to the frontend.
     */
    public YoutubeTokenResponse getAccessTokenInfo() {

        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret) || !StringUtils.hasText(refreshToken)) {
            throw new IllegalStateException(
                    "YouTube OAuth is not configured: set YOUTUBE_CLIENT_ID, YOUTUBE_CLIENT_SECRET and " +
                            "YOUTUBE_REFRESH_TOKEN (see google.oauth.* in application.yml). " +
                            "The refresh token is obtained once via GET /api/youtube/auth/authorize-url " +
                            "followed by the /api/youtube/auth/callback redirect."
            );
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        String response;
        try {
            response = restTemplate.postForObject(
                    "https://oauth2.googleapis.com/token",
                    entity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Typically {"error":"invalid_grant"} - the refresh token was revoked/expired,
                // or the channel owner changed their Google account password.
                throw new IllegalStateException(
                        "Google rejected the refresh token (" + e.getResponseBodyAsString() + "). " +
                                "Re-run the one-time authorization flow to obtain a new YOUTUBE_REFRESH_TOKEN.", e
                );
            }
            throw e;
        }

        try {
            JsonNode json = objectMapper.readTree(response);
            String accessToken = json.get("access_token").asText();
            long expiresIn = json.has("expires_in") ? json.get("expires_in").asLong() : 3600L;
            return new YoutubeTokenResponse(accessToken, expiresIn);
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected response from Google token endpoint: " + response, e);
        }
    }
}
