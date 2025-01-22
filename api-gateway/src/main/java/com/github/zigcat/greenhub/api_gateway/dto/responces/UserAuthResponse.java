package com.github.zigcat.greenhub.api_gateway.dto.responces;

import lombok.*;

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
