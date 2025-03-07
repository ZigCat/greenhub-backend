package com.github.zigcat.greenhub.api_gateway.infrastructure;

import com.github.zigcat.greenhub.api_gateway.domain.schemas.TokenType;

public class InfrastructureDTO {
    public record JwtDTO(
            String token,
            TokenType tokenType
    ){}

    public record UserAuth(
            Long id,
            String email,
            String role,
            String scopes
    ){}
}
