package com.github.zigcat.greenhub.auth_provider.presentation;

import io.swagger.v3.oas.annotations.media.Schema;

public class PresentationDTO {
    @Schema(description = "Registration data transfer object")
    public record UserRegister(
            @Schema(example = "John")
            String fname,
            @Schema(example = "Doe")
            String lname,
            @Schema(example = "johndoe@example.com")
            String email,
            @Schema
            String password
    ){}

    @Schema(description = "API error response")
    public record ApiError(
            @Schema(example = "Internal server error")
            String message,
            @Schema(example = "500")
            int statusCode
    ){}

    @Schema(description = "JWT data transfer object")
    public record JwtToken(
            @Schema
            String token,
            @Schema(example = "REFRESH")
            String tokenType
    ){}
}
