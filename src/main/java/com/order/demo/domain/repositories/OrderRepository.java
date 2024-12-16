package com.order.demo.domain.repositories;

import java.util.Optional;

import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.enums.OrderStatus;
import com.order.demo.domain.valueobjects.ExternalOrderId;
import com.order.demo.domain.valueobjects.OrderId;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
    Optional<Order> findByExternalOrderId(ExternalOrderId externalOrderId);
    void updateStatus(OrderId orderId, OrderStatus status);
}
