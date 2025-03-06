package com.github.zigcat.greenhub.user_provider.application.usecases;

import com.github.zigcat.greenhub.user_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.user_provider.domain.schemas.Role;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {
    public AuthorizationData extractAuthData(ServerHttpRequest request){
        return new AuthorizationData(
                Optional.ofNullable(request.getHeaders().getFirst("X-User-Id"))
                        .map(Long::valueOf)
                        .orElse(null),
                request.getHeaders().getFirst("X-Username"),
                Optional.ofNullable(request.getHeaders().getFirst("X-User-Role"))
                        .map(role -> {
                            try{
                                return Role.valueOf(role);
                            } catch (IllegalArgumentException e){
                                return null;
                            }
                        })
                        .orElse(null),
                request.getHeaders().getFirst("X-User-Scopes")
        );
    }

    public boolean canAccessAccount(AuthorizationData auth, Long id){
        return auth.isAdmin() || auth.getId().equals(id) || auth.getScopes().contains("user.manage");
    }
}
