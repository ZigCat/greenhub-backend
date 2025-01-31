package com.github.zigcat.greenhub.user_provider.dto.rest.messages;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema
public class ApiError {
    @Schema(example = "400")
    private int code;

    @Schema(example = "User not found")
    private String message;
}
