package com.restaurant.management.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    RESTAURANT_OWNER,
    CLIENT;

    @JsonCreator
    public static UserType fromString(String value) {
        if (value == null) {
            return null;
        }
        return UserType.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
