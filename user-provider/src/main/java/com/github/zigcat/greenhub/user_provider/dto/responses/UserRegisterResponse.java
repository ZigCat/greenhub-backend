package com.github.zigcat.greenhub.user_provider.dto.responses;

import com.github.zigcat.greenhub.user_provider.dto.datatypes.DTORequestible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponse implements DTORequestible {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String role;
    private LocalDateTime regDate;
}
