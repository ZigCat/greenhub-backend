package com.github.zigcat.greenhub.auth_provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserKey {
    private Long id;
    private String username;
    private String publicKey;
    private String privateKey;
}
