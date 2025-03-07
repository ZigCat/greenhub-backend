package com.github.zigcat.greenhub.auth_provider.infrastructure;

public class InfrastructureDTO {
    public record UserAuth(
            Long id,
            String email,
            String role,
            String scopes
    ){}

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
}
