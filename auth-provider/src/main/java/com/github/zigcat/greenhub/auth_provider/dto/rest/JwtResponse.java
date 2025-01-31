package com.github.zigcat.greenhub.auth_provider.dto.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema
public class JwtResponse {
    @Schema
    private String access;

    @Schema
    private String refresh;
}
