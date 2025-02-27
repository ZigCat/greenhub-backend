package com.github.zigcat.greenhub.article_provider.domain;

import com.github.zigcat.greenhub.article_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.article_provider.domain.schemas.ScopeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationData {
    private Long id;
    private String username;
    private Role role;
    private String scopes;

    public boolean canPublishArticles(){
        return role == Role.ADMIN
                || role == Role.AUTHOR
                || scopes.contains(ScopeType.ARTICLE_WRITE.getScope());
    }

    public boolean isAdmin(){
        return role == Role.ADMIN;
    }

    public boolean isAuthor(){
        return role == Role.AUTHOR;
    }
}
