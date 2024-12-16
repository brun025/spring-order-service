package com.order.demo.application.dtos;

import java.util.List;

public record CreateOrderRequest(
    String externalOrderId,
    List<CreateOrderItemRequest> items
) {}
