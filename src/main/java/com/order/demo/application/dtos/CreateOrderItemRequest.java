package com.order.demo.application.dtos;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice
) {}