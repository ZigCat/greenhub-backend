package com.github.zigcat.greenhub.auth_provider.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOResponsible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponse implements DTOResponsible {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String role;
    private LocalDateTime regDate;
}
