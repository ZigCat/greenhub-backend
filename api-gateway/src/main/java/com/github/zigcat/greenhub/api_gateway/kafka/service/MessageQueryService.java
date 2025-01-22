package com.github.zigcat.greenhub.api_gateway.kafka.service;

import com.github.zigcat.greenhub.api_gateway.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.api_gateway.dto.requests.JwtRequest;
import com.github.zigcat.greenhub.api_gateway.dto.responces.UserAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageQueryService {
    private final MessageQueryAdapter adapter;

    @Autowired
    public MessageQueryService(MessageQueryAdapter adapter) {
        this.adapter = adapter;
    }

    public Mono<UserAuthResponse> performAuthorizeRequest(JwtRequest token){
        return adapter.performAndAwait(token);
    }
}
