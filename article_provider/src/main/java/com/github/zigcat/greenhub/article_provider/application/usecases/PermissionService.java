package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.article_provider.domain.schemas.Role;
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
    public boolean isAdmin(AuthorizationData auth){
        return auth.isAdmin();
    }

    public boolean isAuthor(AuthorizationData auth){
        return auth.isAuthor();
    }

    public boolean canPublish(AuthorizationData auth){
        return auth.canPublishArticles();
    }

    public boolean canEdit(AuthorizationData auth, Long creator){
        return auth.canPublishArticles() && creator.equals(auth.getId());
    }

    public boolean canDelete(AuthorizationData auth, Long creator){
        return auth.canPublishArticles() && (creator.equals(auth.getId()) || auth.isAdmin());
    }

    public boolean canBePaid(AuthorizationData auth){
        return auth.isAuthor() || auth.isAdmin();
    }
}
