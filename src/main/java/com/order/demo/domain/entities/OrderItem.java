package com.order.demo.domain.entities;

import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.ProductId;

import lombok.Value;

@Value
public class OrderItem {
    ProductId productId;
    String productName;
    int quantity;
    Money unitPrice;
    Money totalPrice;

    public OrderItem(ProductId productId, String productName, int quantity, Money unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(quantity);
    }

    public boolean hasSameIdentityAs(OrderItem other) {
        return this.productId.equals(other.productId);
    }
}