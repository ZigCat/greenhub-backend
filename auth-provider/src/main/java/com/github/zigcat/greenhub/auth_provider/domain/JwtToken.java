package com.github.zigcat.greenhub.auth_provider.domain;

import com.github.zigcat.greenhub.auth_provider.domain.schemas.TokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtToken {
    private String token;
    private TokenType tokenType;
}
