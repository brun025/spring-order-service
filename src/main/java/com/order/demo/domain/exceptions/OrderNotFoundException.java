package com.order.demo.domain.exceptions;

import com.order.demo.domain.valueobjects.OrderId;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(OrderId orderId) {
        super(String.format("Order not found with id: %s", orderId.getValue()));
    }
}
