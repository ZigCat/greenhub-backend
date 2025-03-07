package com.github.zigcat.greenhub.auth_provider.presentation;

public class PresentationDTO {
    public record UserRegister(
            String fname,
            String lname,
            String email,
            String password
    ){}

    public record UserLogin(
            String username,
            String password
    ){}

    public record ApiError(
            String message,
            int statusCode
    ){}
}
