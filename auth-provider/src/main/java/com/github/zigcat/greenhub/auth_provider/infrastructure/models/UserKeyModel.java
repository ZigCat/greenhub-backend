package com.github.zigcat.greenhub.auth_provider.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_key")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserKeyModel {
    @Id
    @Column("user_key_id")
    private Long id;
    private String username;
    @Column("public_key")
    private String publicKey;
    @Column("private_key")
    private String privateKey;

    public UserKeyModel(String username, String publicKey, String privateKey) {
        this.username = username;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
