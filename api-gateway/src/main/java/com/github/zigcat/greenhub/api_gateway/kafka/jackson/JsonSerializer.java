package com.github.zigcat.greenhub.api_gateway.kafka.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.zigcat.greenhub.api_gateway.exceptions.SerDesException;
import com.github.zigcat.greenhub.api_gateway.kafka.dto.KafkaMessageTemplate;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerializer<T> implements Serializer<KafkaMessageTemplate<T>> {
    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public byte[] serialize(String s, KafkaMessageTemplate<T> t) {
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new SerDesException(e.getMessage());
        }
    }
}
