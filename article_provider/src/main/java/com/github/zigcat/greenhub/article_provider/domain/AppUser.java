package com.github.zigcat.greenhub.article_provider.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User domain model")
public class AppUser {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "John")
    private String fname;
    @Schema(example = "Doe")
    private String lname;
    @JsonIgnore
    private String email;
    @Schema(example = "USER")
    private String role;
    @JsonIgnore
    private LocalDateTime regDate;
    @Schema
    private Boolean isVerified;
}
