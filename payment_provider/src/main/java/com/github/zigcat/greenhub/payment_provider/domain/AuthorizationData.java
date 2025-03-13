package com.github.zigcat.greenhub.payment_provider.domain;

import com.github.zigcat.greenhub.payment_provider.domain.schemas.Role;
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

    public boolean isAdmin(){
        return this.role.equals(Role.ADMIN);
    }

    public boolean isAuthor(){
        return this.role.equals(Role.AUTHOR);
    }
}
