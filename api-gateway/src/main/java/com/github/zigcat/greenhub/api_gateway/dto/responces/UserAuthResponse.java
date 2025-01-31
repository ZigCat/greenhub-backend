package com.github.zigcat.greenhub.api_gateway.dto.responces;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponse {
    private Long id;
    private String email;
    private String role;
    private String scopes;
}
