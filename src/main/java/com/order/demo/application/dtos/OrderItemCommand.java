package com.order.demo.application.dtos;

import java.math.BigDecimal;

public record OrderItemCommand(
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice
) {}

