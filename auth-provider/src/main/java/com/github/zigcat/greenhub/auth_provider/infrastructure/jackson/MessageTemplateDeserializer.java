package com.github.zigcat.greenhub.auth_provider.infrastructure.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.auth_provider.domain.MessageTemplate;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

@NoArgsConstructor
public class MessageTemplateDeserializer<T> implements Deserializer<MessageTemplate<T>>{
    private ObjectMapper objectMapper;
    private Class<T> type;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.objectMapper = (ObjectMapper) configs.get("custom.object.mapper");
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        try {
            this.type = (Class<T>) Class.forName((String) configs.get("value.deserializer.type"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageTemplate<T> deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(
                    bytes, objectMapper
                            .getTypeFactory()
                            .constructParametricType(MessageTemplate.class, type)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
