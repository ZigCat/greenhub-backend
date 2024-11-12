package com.github.zigcat.greenhub.api_gateway.adapters;

import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTORequestible;
import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTOResponsible;
import com.github.zigcat.greenhub.api_gateway.security.dto.JwtRequest;
import com.github.zigcat.greenhub.api_gateway.security.dto.UserResponse;

public interface MessageQueryAdapter {
    DTOResponsible performAndAwait(String requestTopic, String replyTopic, DTORequestible data);
    void createTopic(String topicName);
    void deleteTopic(String topicName);
}
