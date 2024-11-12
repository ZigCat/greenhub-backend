package com.github.zigcat.greenhub.api_gateway.security.dto;

import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTOResponsible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements DTOResponsible {
    private String email;
    private String role;
}
