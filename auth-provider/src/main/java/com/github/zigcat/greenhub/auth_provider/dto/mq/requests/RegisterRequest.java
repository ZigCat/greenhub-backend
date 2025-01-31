package com.github.zigcat.greenhub.auth_provider.dto.mq.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class RegisterRequest {
    @Schema(example = "John")
    private String fname;

    @Schema(example = "Doe")
    private String lname;

    @Schema(example = "johndoe@example.com")
    private String email;

    @Schema(example = "JohnDoe_123")
    private String password;
}
