package com.github.zigcat.greenhub.api_gateway.kafka.service;

import com.github.zigcat.greenhub.api_gateway.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.JwtRequest;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageQueryService {
    private final MessageQueryAdapter adapter;
    private final String REQUEST_TOPIC = "auth-topic";
    private final String REPLY_TOPIC = "auth-reply-topic";

    @Autowired
    public MessageQueryService(MessageQueryAdapter adapter) {
        this.adapter = adapter;
    }

    public UserResponse performAuthorizeRequest(JwtRequest token){
        return (UserResponse) adapter.performAndAwait(REQUEST_TOPIC, REPLY_TOPIC, token);
    }
}
