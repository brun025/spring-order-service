package com.order.demo.domain.entities;

import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.ProductId;

import lombok.Value;

@Value
public class Product {
    ProductId id;
    String name;
    Money price;
    
    public Product(ProductId id, String name, Money price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}