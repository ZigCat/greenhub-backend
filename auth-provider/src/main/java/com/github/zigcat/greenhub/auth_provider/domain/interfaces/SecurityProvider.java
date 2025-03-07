package com.github.zigcat.greenhub.auth_provider.domain.interfaces;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;

public interface SecurityProvider {
    String generateAccessToken(AppUser user);
    String generateRefreshToken(AppUser user);
    boolean validateAccessToken(String token);
    boolean validateRefreshToken(String token);
    String getAccessSubject(String token);
}
