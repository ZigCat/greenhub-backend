package com.github.zigcat.greenhub.api_gateway.dto.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {
    private String token;
    private TokenType tokenType;
}
