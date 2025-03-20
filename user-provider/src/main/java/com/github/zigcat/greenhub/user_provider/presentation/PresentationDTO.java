package com.github.zigcat.greenhub.user_provider.presentation;

import io.swagger.v3.oas.annotations.media.Schema;

public class PresentationDTO {
    @Schema(description = "User DTO")
    public record UserDTO(
            @Schema(example = "John")
            String fname,
            @Schema(example = "Doe")
            String lname,
            @Schema(example = "jdoe@example.com")
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
}
