package com.order.demo.domain.events;

import java.math.BigDecimal;
import java.util.List;

import lombok.Value;

@Value
public class ProcessedOrderEvent {
    String externalOrderId;
    String status;
    BigDecimal totalAmount;
    List<ProcessedOrderItemEvent> items;

    @Value
    public static class ProcessedOrderItemEvent {
        String productId;
        String productName;
        int quantity;
        BigDecimal unitPrice;
        BigDecimal totalPrice;
    }
}