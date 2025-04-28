package com.github.zigcat.greenhub.auth_provider.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.SecurityProvider;
import com.github.zigcat.greenhub.auth_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.JwtAuthInfrastructureException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider implements SecurityProvider {
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(@Value("${jwt.secret.refresh}") String jwtRefreshSecret) {
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes());
    }

    @Override
    public String generateAccessToken(AppUser user, Long kid, Key privateKey){
        Instant accessExpirationInstant = LocalDateTime.now().plusMinutes(15)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setHeaderParam("kid", kid)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .claim("scopes", user.getScopes())
                .setIssuedAt(new Date())
                .setExpiration(accessExpiration)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(AppUser user){
        Instant refreshExpirationInstant = LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Mono<AppUser> getAccessClaims(String token, Key publicKey) throws JwtAuthInfrastructureException {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new AppUser(
                    ((Number) claims.get("id")).longValue(),
                    claims.getSubject(),
                    Role.valueOf(claims.get("role").toString()),
                    claims.get("scopes").toString());
        }).onErrorMap(e -> new JwtAuthInfrastructureException("Invalid JWT token"));
    }

    @Override
    public Mono<AppUser> getRefreshClaims(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtRefreshSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new AppUser(((Number) claims.get("id")).longValue(), claims.getSubject());
        }).onErrorMap(e -> new JwtAuthInfrastructureException("Invalid JWT token"));
    }

    @Override
    public Long getAccessKId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT token");
            }
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> headerMap = objectMapper.readValue(headerJson, Map.class);
            Object kidValue = headerMap.get("kid");
            if (kidValue == null) {
                throw new IllegalArgumentException("kid not found in JWT header");
            }
            return Long.parseLong(kidValue.toString());
        } catch (Exception e) {
            throw new JwtAuthInfrastructureException("Invalid token");
        }
    }
}
