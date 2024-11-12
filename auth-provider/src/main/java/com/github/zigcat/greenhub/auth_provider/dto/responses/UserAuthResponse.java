package com.github.zigcat.greenhub.auth_provider.dto.responses;

import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOInstance;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOResponsible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponse implements DTOInstance {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String role;
}
