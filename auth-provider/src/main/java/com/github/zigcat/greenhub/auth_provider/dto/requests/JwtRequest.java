package com.github.zigcat.greenhub.auth_provider.dto.requests;

import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOResponsible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest implements DTOResponsible {
    private String token;
}
