package com.github.zigcat.greenhub.api_gateway.kafka.jackson;

import com.fasterxml.jackson.databind.*;
import com.github.zigcat.greenhub.api_gateway.dto.message.MessageTemplate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@NoArgsConstructor
public class MessageTemplateDeserializer<T> implements Deserializer<MessageTemplate<T>>{
    private ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> type;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.objectMapper = (ObjectMapper) configs.get("custom.object.mapper");
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        try {
            this.type = (Class<T>) Class.forName((String)configs.get("value.deserializer.type"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageTemplate<T> deserialize(String s, byte[] data) {
        try {
            MessageTemplate<T> message = objectMapper
                    .readValue(data, objectMapper
                            .getTypeFactory()
                            .constructParametricType(MessageTemplate.class, type)
                    );
            log.info("Deserialized: "+message.toString());
            return message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
