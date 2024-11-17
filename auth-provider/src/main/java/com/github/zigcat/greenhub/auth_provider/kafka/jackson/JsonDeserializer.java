package com.github.zigcat.greenhub.auth_provider.kafka.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.zigcat.greenhub.auth_provider.dto.datatypes.DTOInstance;
import com.github.zigcat.greenhub.auth_provider.exceptions.SerDesException;
import com.github.zigcat.greenhub.auth_provider.kafka.dto.KafkaMessageTemplate;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class JsonDeserializer<T> implements Deserializer<KafkaMessageTemplate<T>> {
    private final ObjectMapper objectMapper;
    private final Class<T> targetType;

    public JsonDeserializer(Class<T> targetType) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        this.targetType = targetType;
    }

    @Override
    public KafkaMessageTemplate<T> deserialize(String s, byte[] bytes) {
        if(bytes == null) return null;
        try {
            return objectMapper.readValue(
                    bytes,
                    objectMapper
                            .getTypeFactory()
                            .constructParametricType(
                                    KafkaMessageTemplate.class,
                                    targetType
                            ));
        } catch (IOException e) {
            throw new SerDesException(e.getMessage());
        }
    }
}
