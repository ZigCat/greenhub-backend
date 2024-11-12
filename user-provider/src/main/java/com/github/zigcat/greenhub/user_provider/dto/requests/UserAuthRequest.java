package com.github.zigcat.greenhub.user_provider.dto.requests;

import com.github.zigcat.greenhub.user_provider.dto.datatypes.DTOResponsible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthRequest implements DTOResponsible {
    private String username;
}
