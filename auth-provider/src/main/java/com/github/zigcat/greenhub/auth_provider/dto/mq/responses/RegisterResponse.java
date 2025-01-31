package com.github.zigcat.greenhub.auth_provider.dto.mq.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema
public class RegisterResponse {
    @Schema(example = "1")
    private Long id;

    @Schema(example = "John")
    private String fname;

    @Schema(example = "Doe")
    private String lname;

    @Schema(example = "johndoe@example.com")
    private String email;

    @Schema(example = "USER")
    private String role;

    @Schema
    private LocalDateTime regDate;
}
