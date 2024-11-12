package com.github.zigcat.greenhub.user_provider.events;

import com.github.zigcat.greenhub.user_provider.dto.responses.UserAuthResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeAuthServiceReply {
    private UserAuthResponse response;
}
