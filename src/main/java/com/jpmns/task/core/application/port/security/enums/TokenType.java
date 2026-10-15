package com.jpmns.task.core.application.port.security.enums;

public enum TokenType {

    ACCESS("access"),
    REFRESH("refresh");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TokenType fromValue(String value) {
        for (var type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }

        return null;
    }
}
