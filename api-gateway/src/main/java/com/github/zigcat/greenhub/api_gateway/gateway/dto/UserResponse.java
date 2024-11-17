package com.github.zigcat.greenhub.api_gateway.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String role;
}
