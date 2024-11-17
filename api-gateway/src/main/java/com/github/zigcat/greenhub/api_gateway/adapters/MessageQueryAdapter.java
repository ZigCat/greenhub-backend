package com.github.zigcat.greenhub.api_gateway.adapters;

import com.github.zigcat.greenhub.api_gateway.gateway.dto.JwtRequest;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.UserResponse;

public interface MessageQueryAdapter {
    UserResponse performAndAwait(String requestTopic, String replyTopic, JwtRequest data);
    void createTopic(String topicName);
    void deleteTopic(String topicName);
}
