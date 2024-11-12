package com.github.zigcat.greenhub.auth_provider.kafka.service;

import com.github.zigcat.greenhub.auth_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.auth_provider.dto.requests.UserAuthRequest;
import com.github.zigcat.greenhub.auth_provider.dto.requests.UserRegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.responses.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.dto.responses.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MessageQueryService {
    private final MessageQueryAdapter adapter;

    @Autowired
    public MessageQueryService(MessageQueryAdapter adapter) {
        this.adapter = adapter;
    }

    public UserAuthResponse performAuthorizeRequest(UserAuthRequest request){
        String REQUEST_TOPIC = "auth-user-topic";
        String REPLY_TOPIC = "auth-user-reply-topic";
        return (UserAuthResponse) adapter.sendAndAwait(REQUEST_TOPIC, REPLY_TOPIC, request);
    }

    public UserRegisterResponse performRegisterRequest(UserRegisterRequest request){
        String REQUEST_TOPIC = "reg-topic";
        String REPLY_TOPIC = "reg-reply-topic";
        return (UserRegisterResponse) adapter.sendAndAwait(REQUEST_TOPIC, REPLY_TOPIC, request);
    }
}
