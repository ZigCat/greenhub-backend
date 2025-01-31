package com.github.zigcat.greenhub.user_provider.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "User Entity")
public class AppUser {
    @Id
    @Column("user_id")
    @Schema(example = "1")
    private Long id;

    @Column("first_name")
    @Schema(example = "John")
    private String fname;

    @Column("last_name")
    @Schema(example = "Doe")
    private String lname;

    @Schema(example = "johndoe@example.com")
    private String email;

    @JsonIgnore
    @Schema
    private String password;

    @Schema(example = "USER")
    private String role;

    @Column("reg_date")
    @Schema
    private LocalDateTime regDate;

    public AppUser(String fname, String lname, String email, String password, String role) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.regDate = LocalDateTime.now();
    }

    public Role getRole(){
        return Role.valueOf(role);
    }
}
