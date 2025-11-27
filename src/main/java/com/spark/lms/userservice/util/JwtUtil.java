package com.spark.lms.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    // SAME KEY USED IN API GATEWAY
    private final SecretKey key = Keys.hmacShaKeyFor(
            "MySecretKeyForJWTTokenGenerationNeedsToBeLongEnough".getBytes()
    );

    // Token validity: 24 hours
    private final long validityInMillis = 24 * 60 * 60 * 1000;

    // Generate JWT token
    public String generateToken(UUID userId, String email, String role) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMillis);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // Extract Claims
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        return getClaims(token).get("userId", String.class);
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }
}
