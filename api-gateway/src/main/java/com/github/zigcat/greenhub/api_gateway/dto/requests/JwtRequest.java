package com.github.zigcat.greenhub.api_gateway.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for transferring JWT through Kafka")
public class JwtRequest {
    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Type of JWT token", example = "ACCESS")
    private TokenType tokenType;
}
