package com.github.zigcat.greenhub.user_provider.dto.requests;

import com.github.zigcat.greenhub.user_provider.dto.datatypes.DTOResponsible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest implements DTOResponsible {
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String role;
}
