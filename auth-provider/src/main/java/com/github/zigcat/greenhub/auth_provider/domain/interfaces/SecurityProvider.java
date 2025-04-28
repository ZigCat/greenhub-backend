package com.github.zigcat.greenhub.auth_provider.domain.interfaces;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import reactor.core.publisher.Mono;

import java.security.Key;

public interface SecurityProvider {
    String generateAccessToken(AppUser user, Long kid, Key privateKey);
    String generateRefreshToken(AppUser user);
    Mono<AppUser> getAccessClaims(String token, Key publicKey);
    Mono<AppUser> getRefreshClaims(String token);
    Long getAccessKId(String token);
}
