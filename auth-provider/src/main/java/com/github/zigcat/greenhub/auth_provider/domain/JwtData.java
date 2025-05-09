package com.github.zigcat.greenhub.auth_provider.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "JWT Response domain model")
public class JwtData {
    @Schema
    private String access;
    @Schema
    private String refresh;
    private AppUser user;
}
