package com.github.zigcat.greenhub.auth_provider.application.events;

import com.github.zigcat.greenhub.auth_provider.infrastructure.adapter.dto.UserAuthResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeReply {
    private UserAuthResponse userResponse;
}
