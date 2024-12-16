package com.order.demo.domain.valueobjects;

import lombok.Value;


@Value
public class ProductId {
    String value;
    
    public ProductId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        this.value = value;
    }
}