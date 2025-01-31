package com.github.zigcat.greenhub.user_provider.dto.mq.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponse {
    private Long id;
    private String email;
    private String role;
    private String scopes;
}