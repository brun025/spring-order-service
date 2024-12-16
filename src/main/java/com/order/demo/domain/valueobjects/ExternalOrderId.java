package com.order.demo.domain.valueobjects;

import lombok.Value;

@Value
public class ExternalOrderId {
    String value;
    
    public ExternalOrderId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("External order ID cannot be empty");
        }
        this.value = value;
    }

}