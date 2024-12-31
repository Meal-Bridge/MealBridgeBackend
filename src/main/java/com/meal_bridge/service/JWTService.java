package com.meal_bridge.service;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    private String SECRET_KEY = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    private String ROLE = "role";

    public String extractEmail(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return extractClaims(token, (claims) -> claims.getSubject());
    }
    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(ROLE, String.class);
    }
    public boolean isValid(String token, UserDetails userDetails) {

        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {

        return extractClaims(token, (claims) -> claims.getExpiration());
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token).getPayload();
    }

    public String generateToken(String email, String role) {

        String token = Jwts
                .builder()
                .subject(email)
                .claim(ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(getSigninKey())
                .compact();

        return token;
    }

    private SecretKey getSigninKey() {

        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
