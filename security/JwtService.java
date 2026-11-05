package com.todo.todoList.multitenancy.security;

import com.todo.todoList.multitenancy.identity.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()

                .subject(user.getUserId().toString())

                .claim("userId", user.getUserId())
                .claim("tenantId", user.getTenant().getTenantId())
                .claim("companyCode", user.getTenant().getCompanyCode())
                .claim("role", user.getRole())

                .issuedAt(now)
                .expiration(expiryDate)

                .signWith(getSigningKey())

                .compact();
    }

    // Extract all claims after verifying the JWT signature
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // Extract a particular claim
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }


    // Extract user ID
    public Long extractUserId(String token) {

        return Long.valueOf(
                extractClaim(token, Claims::getSubject)
        );
    }


    // Extract tenant ID
    public Long extractTenantId(String token) {

        Number tenantId = extractClaim(
                token,
                claims -> claims.get("tenantId", Number.class)
        );

        return tenantId.longValue();
    }


    // Extract role
    public String extractRole(String token) {

        return extractClaim(
                token,
                claims -> claims.get("role", String.class)
        );
    }


    // Validate JWT
    public boolean isTokenValid(String token) {

        try {
            extractAllClaims(token);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}