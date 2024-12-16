package com.order.demo.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.order.demo.application.dtos.CreateOrderCommand;
import com.order.demo.application.dtos.OrderItemCommand;
import com.order.demo.domain.valueobjects.ExternalOrderId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingOrderEvent {
    private String externalOrderId; // Mudado para String
    private List<OrderItemData> items;
    private LocalDateTime receivedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemData {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
    }

    public CreateOrderCommand toCommand() {
        List<OrderItemCommand> itemCommands = items.stream()
            .map(item -> new OrderItemCommand(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice()
            ))
            .collect(Collectors.toList());

        return new CreateOrderCommand(
            new ExternalOrderId(this.externalOrderId), // Convertendo para Value Object após deserialização
            itemCommands
        );
    }
}