package com.github.zigcat.greenhub.api_gateway.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessageTemplate<T> {
    private T payload;
    private int status;
    private String message;

    public KafkaMessageTemplate(T payload) {
        this.payload = payload;
        this.status = 200;
        this.message = null;
    }

    public KafkaMessageTemplate(int status, String message) {
        this.status = status;
        this.message = message;
        this.payload = null;
    }
}
