package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.Role;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    public AuthorizationData extractAuthData(ServerHttpRequest request){
        return new AuthorizationData(
                Long.valueOf(request.getHeaders().getFirst("X-User-Id")),
                request.getHeaders().getFirst("X-Username"),
                Role.valueOf(request.getHeaders().getFirst("X-User-Role")),
                request.getHeaders().getFirst("X-User-Scopes")
        );
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

    public boolean canEdit(AuthorizationData auth, Article entity){
        return auth.canPublishArticles() && entity.getCreator().equals(auth.getId());
    }

    public boolean canDelete(AuthorizationData auth, Article entity){
        return auth.canPublishArticles() && (entity.getCreator().equals(auth.getId()) || auth.isAdmin());
    }

    public PaidStatus canBePaid(AuthorizationData auth){
        return auth.isAuthor() ? PaidStatus.PAID : PaidStatus.FREE;
    }
}
