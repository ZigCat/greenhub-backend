package com.github.zigcat.greenhub.api_gateway.domain;

import com.github.zigcat.greenhub.api_gateway.domain.schemas.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {
    private Long id;
    private String email;
    private Role role;
    private String scopes;
}
