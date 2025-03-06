package com.github.zigcat.greenhub.user_provider.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.zigcat.greenhub.user_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.user_provider.domain.schemas.ScopeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
