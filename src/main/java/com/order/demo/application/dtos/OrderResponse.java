package com.order.demo.application.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.enums.OrderStatus;

public record OrderResponse(
    UUID id,
    String externalOrderId,
    OrderStatus status,
    BigDecimal totalAmount,
    List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId().getValue(),
            order.getExternalOrderId().getValue(),
            order.getStatus(),
            order.getTotalAmount().getAmount(),
            order.getItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList())
        );
    }
}
