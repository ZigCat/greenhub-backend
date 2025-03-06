package com.github.zigcat.greenhub.user_provider.presentation;

import io.swagger.v3.oas.annotations.media.Schema;

public class PresentationDTO {
    public record UserDTO(
            String fname,
            String lname,
            String email,
            String password
    ){}

    public record ApiError(
            String message,
            int statusCode
    ){}
}
