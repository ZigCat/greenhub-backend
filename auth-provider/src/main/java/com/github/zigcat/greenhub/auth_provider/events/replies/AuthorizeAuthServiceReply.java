package com.github.zigcat.greenhub.auth_provider.events.replies;

import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.UserAuthResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeAuthServiceReply {
    private UserAuthResponse userResponse;
}
