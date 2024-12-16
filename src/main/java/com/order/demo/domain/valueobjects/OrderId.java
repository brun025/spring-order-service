package com.order.demo.domain.valueobjects;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor()
public class OrderId {
    @JsonValue
    private final UUID value;

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        return new OrderId(value);
    }

    // Necessário para alguns frameworks
    public UUID getId() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

