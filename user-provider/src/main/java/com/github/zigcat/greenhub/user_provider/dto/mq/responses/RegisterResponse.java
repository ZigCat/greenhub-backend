package com.github.zigcat.greenhub.user_provider.dto.mq.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String role;
    private LocalDateTime regDate;
}