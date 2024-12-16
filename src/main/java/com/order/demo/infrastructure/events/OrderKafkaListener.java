package com.order.demo.infrastructure.events;

import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.order.demo.application.dtos.CreateOrderCommand;
import com.order.demo.application.dtos.OrderItemCommand;
import com.order.demo.application.usecases.CreateOrderUseCase;
import com.order.demo.domain.events.IncomingOrderEvent;
import com.order.demo.domain.valueobjects.ExternalOrderId;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaListener {
    private final CreateOrderUseCase createOrderUseCase;

    @KafkaListener(topics = "incoming-orders")
    public void handleIncomingOrder(IncomingOrderEvent event) {
        try {
            CreateOrderCommand command = new CreateOrderCommand(
                new ExternalOrderId(event.getExternalOrderId()),
                event.getItems().stream()
                    .map(item -> new OrderItemCommand(item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice()))
                    .collect(Collectors.toList())
            );
            
            createOrderUseCase.execute(command);
        } catch (Exception e) {
            log.error("Error processing incoming order: {}", event.getExternalOrderId(), e);
        }
    }
}
