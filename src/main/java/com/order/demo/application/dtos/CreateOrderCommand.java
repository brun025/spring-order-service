package com.order.demo.application.dtos;

import java.util.List;

import com.order.demo.domain.valueobjects.ExternalOrderId;

public record CreateOrderCommand(
    ExternalOrderId externalOrderId,
    List<OrderItemCommand> items
) {}

