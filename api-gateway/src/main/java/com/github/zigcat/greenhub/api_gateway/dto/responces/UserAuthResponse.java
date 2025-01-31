package com.github.zigcat.greenhub.api_gateway.dto.responces;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication server response with User Auth data")
public class UserAuthResponse {
    @Schema(description = "User's ID", example = "1")
    private Long id;

    @Schema(description = "User's email (username)", example = "johndoe@example.com")
    private String email;

    @Schema(description = "User's application role", example = "USER")
    private String role;

    @Schema(description = "User's access scopes", example = "users.read articles.read")
    private String scopes;
}
