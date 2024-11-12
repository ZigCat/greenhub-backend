package com.github.zigcat.greenhub.api_gateway.security.user;

import com.github.zigcat.greenhub.api_gateway.security.dto.UserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class JwtUserDetails implements UserDetails {
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(UserResponse user) {
        this.authorities = List.of(
                new SimpleGrantedAuthority(user.getRole())
        );
        this.username = user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
