package com.github.zigcat.greenhub.api_gateway.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageTemplate<T> {
    private T payload;
    private int status;
    private String message;

    public MessageTemplate(T payload) {
        this.payload = payload;
        this.status = 200;
        this.message = null;
    }

    public MessageTemplate(int status, String message) {
        this.status = status;
        this.message = message;
        this.payload = null;
    }
}