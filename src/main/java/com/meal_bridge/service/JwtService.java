package com.meal_bridge.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import com.meal_bridge.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private Long refreshExpiration;

    public String extractUsername(String token) {
        token = cleanToken(token);
        return extractClaims(token, Claims::getSubject);
    }

    public String extractRoles(String token) {
        token = cleanToken(token);
        return extractClaims(token, claims -> claims.get("roles", String.class));
    }

    public Integer extractUserId(String token) {
        token = cleanToken(token);
        return extractClaims(token, claims -> claims.get("userId", Integer.class));
    }

    public boolean isValid(String token, UserDetails userDetails) {
        try {
            token = cleanToken(token);
            String username = extractUsername(token);
            boolean isValid = isTokenExpired(token) && username.equals(userDetails.getUsername());

            if (!isValid) {
                log.warn("Invalid token attempt for user: {}", username);
            }

            return isValid;
        } catch (JwtException e) {

            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        return buildToken(extraClaims, user, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, Long expiration) {

        extraClaims.put("roles", user.getRoles().toString());
        extraClaims.put("username", user.getEmail());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Failed to generate signing key: {}", e.getMessage());
            throw new RuntimeException("Failed to generate JWT signing key", e);
        }
    }

    private String cleanToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    public boolean isTokenExpired(String token) {
        try {
            token = cleanToken(token);
            return !extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return false;
        }
    }

    public Date getTokenExpiration(String token) {
        token = cleanToken(token);
        return extractExpiration(token);
    }

    public boolean isValidRefreshToken(String token, UserDetails userDetails) {
        try {
            token = cleanToken(token);
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && isTokenExpired(token);
        } catch (JwtException e) {
            log.error("Refresh token validation error: {}", e.getMessage());
            return false;
        }
    }
}