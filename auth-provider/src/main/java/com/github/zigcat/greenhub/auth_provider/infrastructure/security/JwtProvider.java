package com.github.zigcat.greenhub.auth_provider.infrastructure.security;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.SecurityProvider;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.JwtAuthInfrastructureException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider implements SecurityProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ){
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    @Override
    public String generateAccessToken(AppUser user){
        Instant accessExpirationInstant = LocalDateTime.now().plusMinutes(10)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("id", user.getId())
                .compact();
    }

    @Override
    public String generateRefreshToken(AppUser user){
        Instant refreshExpirationInstant = LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    @Override
    public boolean validateAccessToken(String token) throws JwtAuthInfrastructureException {
        return validateToken(token, jwtAccessSecret);
    }

    @Override
    public boolean validateRefreshToken(String token){
        return validateToken(token, jwtRefreshSecret);
    }

    @Override
    public String getAccessSubject(String token){
        return getSubject(token, jwtAccessSecret);
    }

    private Claims getClaims(String token, SecretKey key){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String getSubject(String token, SecretKey key){
        return getClaims(token, key).getSubject();
    }

    private boolean validateToken(String token, SecretKey key) throws JwtAuthInfrastructureException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new JwtAuthInfrastructureException("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            throw new JwtAuthInfrastructureException("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthInfrastructureException("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            throw new JwtAuthInfrastructureException("JWT claims string is empty");
        } catch (SecurityException e) {
            throw new JwtAuthInfrastructureException("Invalid JWT signature");
        } catch (Exception e) {
            throw new JwtAuthInfrastructureException("Invalid token");
        }
    }
}
