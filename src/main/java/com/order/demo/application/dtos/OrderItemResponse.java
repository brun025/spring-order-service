package com.order.demo.application.dtos;

import java.math.BigDecimal;

import com.order.demo.domain.entities.OrderItem;

public record OrderItemResponse(
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
            item.getProductId().getValue(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice().getAmount(),
            item.getTotalPrice().getAmount()
        );
    }
}