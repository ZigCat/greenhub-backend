package com.github.zigcat.greenhub.api_gateway.kafka.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.zigcat.greenhub.api_gateway.dto.datatypes.DTOInstance;
import com.github.zigcat.greenhub.api_gateway.exceptions.SerDesException;
import com.github.zigcat.greenhub.api_gateway.kafka.dto.KafkaMessageTemplate;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class JsonDeserializer<T> implements Deserializer<KafkaMessageTemplate<T>> {
    private final ObjectMapper objectMapper;

    public JsonDeserializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
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
                                    DTOInstance.class
                            ));
        } catch (IOException e) {
            throw new SerDesException(e.getMessage());
        }
    }
}
