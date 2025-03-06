package com.github.zigcat.greenhub.user_provider.domain;

import com.github.zigcat.greenhub.user_provider.domain.schemas.ScopeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scope {
    private Long id;
    private Long userId;
    private String scope;

    public Scope(Long userId, String scope) {
        this.userId = userId;
        this.scope = scope;
    }

    public static List<Scope> defaultScopes(Long userId){
        return List.of(
                new Scope(userId, ScopeType.USER_READ.getScope()),
                new Scope(userId, ScopeType.ARTICLE_READ.getScope())
        );
    }
}
