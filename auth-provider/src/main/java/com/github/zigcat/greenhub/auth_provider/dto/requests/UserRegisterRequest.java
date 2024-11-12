package com.github.zigcat.greenhub.auth_provider.dto.requests;

import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTORequestible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest implements DTORequestible {
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String role;
}
