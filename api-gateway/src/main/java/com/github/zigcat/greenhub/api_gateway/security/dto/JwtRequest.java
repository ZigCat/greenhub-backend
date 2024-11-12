package com.github.zigcat.greenhub.api_gateway.security.dto;

import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTORequestible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest implements DTORequestible {
    private String token;
}
