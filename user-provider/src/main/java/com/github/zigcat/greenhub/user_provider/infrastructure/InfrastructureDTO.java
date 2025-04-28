package com.github.zigcat.greenhub.user_provider.infrastructure;

import java.time.LocalDateTime;

public class InfrastructureDTO {
    public record UserAuth(
            Long id,
            String fname,
            String lname,
            String email,
            LocalDateTime regDate,
            String role,
            String scopes
    ){}
}
