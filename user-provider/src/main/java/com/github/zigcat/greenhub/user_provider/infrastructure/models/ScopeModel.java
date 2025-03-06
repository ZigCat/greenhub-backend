package com.github.zigcat.greenhub.user_provider.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_scopes")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScopeModel {
    @Column("scope_id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("scopes")
    private String scope;

    public ScopeModel(Long userId, String scope) {
        this.userId = userId;
        this.scope = scope;
    }
}
