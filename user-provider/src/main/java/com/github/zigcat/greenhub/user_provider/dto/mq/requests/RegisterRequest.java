package com.github.zigcat.greenhub.user_provider.dto.mq.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String fname;
    private String lname;
    private String email;
    private String password;
}