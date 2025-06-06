package com.github.zigcat.greenhub.api_gateway.domain.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.zigcat.greenhub.api_gateway.application.exceptions.ServerErrorAppException;

public enum TokenType {
    ACCESS("access"),
    REFRESH("refresh");
    private String value;

    TokenType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TokenType forValue(String value) {
        for (TokenType tokenType : values()) {
            if (tokenType.value.equals(value)) {
                return tokenType;
            }
        }
        throw new ServerErrorAppException("Unable to deserialize TokenType");
    }
}
