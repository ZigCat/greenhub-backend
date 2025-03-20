package com.github.zigcat.greenhub.user_provider.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.zigcat.greenhub.user_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.user_provider.domain.schemas.ScopeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "USER domain model")
public class AppUser {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "John")
    private String fname;
    @Schema(example = "Doe")
    private String lname;
    @Schema(example = "jdoe@example.com")
    private String email;
    @JsonIgnore
    private String password;
    @Schema(example = "USER")
    private Role role;
    @Schema(example = "2025-03-17T13:53:33.149282")
    private LocalDateTime regDate;
    @Schema
    private String scopes;

    public AppUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AppUser(Long id, String fname, String lname, String email, String password, Role role, LocalDateTime regDate) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.regDate = regDate;
    }

    public AppUser(Long id, String email, Role role, String scopes) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.scopes = scopes;
    }

    public AppUser(String fname, String lname, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
    }
}
