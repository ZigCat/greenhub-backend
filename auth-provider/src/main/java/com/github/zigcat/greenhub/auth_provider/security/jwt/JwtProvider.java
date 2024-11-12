package com.github.zigcat.greenhub.auth_provider.security.jwt;

import com.github.zigcat.greenhub.auth_provider.dto.responses.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.exceptions.JwtAuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ){
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(@NonNull UserAuthResponse user){
        Instant accessExpirationInstant = LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("id", user.getId())
                .claim("fname", user.getFname())
                .claim("lname", user.getLname())
                .compact();
    }

    public String generateRefreshToken(@NonNull UserAuthResponse user){
        Instant refreshExpirationInstant = LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(String token){
        return validateToken(token, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String token){
        return validateToken(token, jwtRefreshSecret);
    }

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

    private boolean validateToken(String token, SecretKey key) throws JwtAuthException{
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new JwtAuthException("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new JwtAuthException("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthException("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JwtAuthException("JWT claims string is empty: " + e.getMessage());
        } catch (SecurityException e) {
            throw new JwtAuthException("Invalid JWT signature: " + e.getMessage());
        } catch (Exception e) {
            throw new JwtAuthException("Invalid token: " + e.getMessage());
        }
    }
}
