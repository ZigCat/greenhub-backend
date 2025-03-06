package com.github.zigcat.greenhub.user_provider.infrastructure.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.zigcat.greenhub.user_provider.domain.schemas.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "User Entity")
public class UserModel {
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
    private Role role;

    @Column("reg_date")
    @Schema
    private LocalDateTime regDate;

    public UserModel(String fname, String lname, String email, String password, Role role, LocalDateTime regDate) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.regDate = regDate;
    }
}
