package com.example.demo.config;

import com.example.demo.management.authentication.dto.AuthenticationDetailsDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-token-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${security.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    public String extractUserID(String token) {
        return extractClaim(token, Claims::getId);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims == null) {
                System.err.println("Claims are null!");
                return null;
            }
            System.out.println("Claims: " + claims);  // Log to check claims
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            System.err.println("Error extracting claim: " + e.getMessage());
            return null;
        }
    }


    public String generateToken(AuthenticationDetailsDto userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, AuthenticationDetailsDto userDetails) {
        return buildToken(extraClaims, userDetails, accessTokenExpirationTime);
    }

    public String generateTokenWitClaims(String token, boolean isRefreshToken) {
        Claims claims = extractAllClaims(token);
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + (isRefreshToken ? refreshTokenExpirationTime : accessTokenExpirationTime));

        return Jwts.builder()
                .setId(Optional.ofNullable(claims.get("id")).map(Object::toString).orElse("UNKNOWN")) // ✅ Avoid NPE
                .setSubject(claims.getSubject())
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(getSignInKey())
                .compact();
    }

    public String generateToken(Authentication authentication, boolean isRefreshToken) {
        AuthenticationDetailsDto authenticationDetails = (AuthenticationDetailsDto) authentication.getPrincipal();
        String username = authentication.getName();
        Map<String, Object> claims = getStringObjectMap(authenticationDetails, username);

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + (isRefreshToken ? refreshTokenExpirationTime : accessTokenExpirationTime));

        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(getSignInKey())
                .compact();
    }

    private static Map<String, Object> getStringObjectMap(AuthenticationDetailsDto authenticationDetails, String username) {
        Long userId = authenticationDetails.getId();
        Long employeeId = authenticationDetails.getEmployeeId();

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("id", userId);
        claims.put("employeeId", employeeId);

        return claims;
    }

    private String buildToken(Map<String, Object> extraClaims, AuthenticationDetailsDto userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, AuthenticationDetailsDto userDetails) {
        final String username = extractUsername(token);
        return !isTokenExpired(token) && username.equals(userDetails.getUsername());
    }

    public boolean isTokenValid(String token, String userName) {
        final String username = extractUsername(token);
        return !isTokenExpired(token) && username.equals(userName);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // ✅ Corrected
                .build()
                .parseClaimsJws(token) // ✅ Correct method for parsing JWT
                .getBody();
    }

    private Key getSignInKey() { // ✅ Changed return type from SecretKey to Key
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey()) // ✅ Corrected
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Token is invalid or expired
        }
    }
}
