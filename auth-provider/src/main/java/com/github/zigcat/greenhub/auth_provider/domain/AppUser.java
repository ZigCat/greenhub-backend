package com.github.zigcat.greenhub.auth_provider.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.zigcat.greenhub.auth_provider.domain.schemas.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    @JsonIgnore
    private String password;
    private Role role;
    private LocalDateTime regDate;
    private String scopes;

    public AppUser(String fname, String lname, String email, String password) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
    }

    public AppUser(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
