package com.example.demo.youtube;

/**
 * What the frontend receives from GET /api/youtube/token.
 *
 * accessToken - short-lived Google OAuth2 access token (Bearer), valid for expiresIn seconds.
 * expiresIn   - seconds until accessToken expires, as reported by Google (normally 3600).
 */
public record YoutubeTokenResponse(String accessToken, long expiresIn) {
}
