package com.github.zigcat.greenhub.user_provider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String role;
}
