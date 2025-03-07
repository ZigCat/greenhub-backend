package com.github.zigcat.greenhub.auth_provider.infrastructure.adapter.dto;

import com.github.zigcat.greenhub.auth_provider.domain.schemas.TokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {
    private String token;
    private TokenType tokenType;
}
