package com.github.zigcat.greenhub.api_gateway.gateway.services;

import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import com.github.zigcat.greenhub.api_gateway.exceptions.ServerException;
import com.github.zigcat.greenhub.api_gateway.kafka.service.MessageQueryService;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.UserResponse;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.JwtRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final MessageQueryService messageQueryService;

    @Autowired
    public AuthService(MessageQueryService messageQueryService) {
        this.messageQueryService = messageQueryService;
    }

    public UserResponse authorizeByToken(String token) throws AuthException, ServerException {
        JwtRequest request = new JwtRequest(token);
        UserResponse response = messageQueryService.performAuthorizeRequest(request);
        if(response == null){
            throw new AuthException();
        }
        return response;
    }
}
