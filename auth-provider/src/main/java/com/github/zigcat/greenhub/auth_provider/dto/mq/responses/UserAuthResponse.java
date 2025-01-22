package com.github.zigcat.greenhub.auth_provider.dto.mq.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponse {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String role;
}
