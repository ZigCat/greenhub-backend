package com.github.zigcat.greenhub.user_provider.infrastructure.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.user_provider.domain.MessageTemplate;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;

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
