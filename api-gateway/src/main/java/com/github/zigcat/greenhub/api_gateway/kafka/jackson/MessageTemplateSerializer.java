package com.github.zigcat.greenhub.api_gateway.kafka.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.zigcat.greenhub.api_gateway.dto.message.MessageTemplate;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@NoArgsConstructor
public class MessageTemplateSerializer<T> implements Serializer<MessageTemplate<T>> {
    private ObjectMapper objectMapper;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.objectMapper = (ObjectMapper) configs.get("custom.object.mapper");
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
    }

    @Override
    public byte[] serialize(String s, MessageTemplate<T> data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
