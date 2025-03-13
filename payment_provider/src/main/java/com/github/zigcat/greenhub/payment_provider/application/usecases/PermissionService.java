package com.github.zigcat.greenhub.payment_provider.application.usecases;

import com.github.zigcat.greenhub.payment_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ScopeType;
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

    public boolean isAuthPresent(AuthorizationData auth){
        return auth.getId() != null
                && auth.getUsername() != null
                && auth.getRole() != null
                && auth.getScopes() != null;
    }

    public boolean canViewPayments(AuthorizationData auth){
        return auth.getScopes().contains(ScopeType.PAYMENT_VIEW.getScope());
    }
}
